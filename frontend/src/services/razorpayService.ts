import { api } from './api';
import { Payment, RazorpayOrderResponse, RazorpayVerificationRequest } from '../types';

export interface RazorpayOptions {
  key: string;
  amount: number;
  currency: string;
  name: string;
  description: string;
  image?: string;
  order_id: string;
  handler: (response: {
    razorpay_payment_id: string;
    razorpay_order_id: string;
    razorpay_signature: string;
  }) => void;
  prefill?: {
    name?: string;
    email?: string;
    contact?: string;
  };
  notes?: Record<string, string>;
  theme?: {
    color?: string;
    backdrop_color?: string;
  };
  modal?: {
    ondismiss?: () => void;
    escape?: boolean;
    backdropclose?: boolean;
  };
}

let scriptLoadingPromise: Promise<boolean> | null = null;

export const razorpayService = {
  /**
   * Dynamically loads the Razorpay checkout script with caching.
   */
  loadScript(): Promise<boolean> {
    if (typeof window !== 'undefined' && (window as any).Razorpay) {
      return Promise.resolve(true);
    }

    if (scriptLoadingPromise) {
      return scriptLoadingPromise;
    }

    scriptLoadingPromise = new Promise<boolean>((resolve) => {
      const script = document.createElement('script');
      script.src = 'https://checkout.razorpay.com/v1/checkout.js';
      script.async = true;
      script.onload = () => resolve(true);
      script.onerror = () => {
        console.error('Failed to load Razorpay Checkout script.');
        scriptLoadingPromise = null;
        resolve(false);
      };
      document.body.appendChild(script);
    });

    return scriptLoadingPromise;
  },

  /**
   * Requests the backend to create a Razorpay order.
   */
  async createOrder(orderId: number): Promise<RazorpayOrderResponse> {
    return api.post('/api/payments/razorpay/create-order', { orderId });
  },

  /**
   * Cryptographically verifies the payment signature with the backend.
   */
  async verifyPayment(data: RazorpayVerificationRequest): Promise<Payment> {
    return api.post('/api/payments/razorpay/verify', data);
  },

  /**
   * Opens the Razorpay Checkout Modal with luxury branding.
   */
  async openCheckout(
    orderDetails: RazorpayOrderResponse,
    onSuccess: (payment: Payment) => void,
    onFailure: (errorMsg: string) => void,
    onDismiss?: () => void
  ): Promise<void> {
    const isLoaded = await this.loadScript();
    if (!isLoaded) {
      onFailure('Payment gateway script failed to load. Please check your connection or retry.');
      return;
    }

    const options: RazorpayOptions = {
      key: orderDetails.keyId,
      amount: orderDetails.amountInPaise,
      currency: orderDetails.currency || 'INR',
      name: 'JPBazaar Luxury Boutique',
      description: `Settlement for Order #${orderDetails.orderId}`,
      order_id: orderDetails.razorpayOrderId,
      prefill: {
        name: orderDetails.customerName || 'JPBazaar Patron',
        email: orderDetails.customerEmail || '',
        contact: orderDetails.customerContact || '',
      },
      theme: {
        color: '#D4AF37', // JPBazaar signature metallic gold
        backdrop_color: 'rgba(10, 10, 10, 0.85)',
      },
      handler: async (response) => {
        try {
          const verificationRequest: RazorpayVerificationRequest = {
            orderId: orderDetails.orderId,
            razorpayOrderId: response.razorpay_order_id,
            razorpayPaymentId: response.razorpay_payment_id,
            razorpaySignature: response.razorpay_signature,
          };

          const verifiedPayment = await razorpayService.verifyPayment(verificationRequest);
          onSuccess(verifiedPayment);
        } catch (err: any) {
          console.error('Payment verification failed:', err);
          onFailure(err.message || 'Cryptographic verification failed. Please contact support.');
        }
      },
      modal: {
        ondismiss: () => {
          if (onDismiss) onDismiss();
        },
        escape: true,
        backdropclose: false,
      },
    };

    const rzp = new (window as any).Razorpay(options);
    rzp.on('payment.failed', (response: any) => {
      console.warn('Razorpay payment failed callback:', response.error);
      onFailure(response.error?.description || 'Transaction declined by issuer.');
    });
    rzp.open();
  },
};
