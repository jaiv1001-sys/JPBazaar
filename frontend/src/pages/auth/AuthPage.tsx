import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';
import { Input } from '../../components/common/Input';
import { Button } from '../../components/common/Button';
import { Card } from '../../components/common/Card';
import { Lock, Mail, User as UserIcon, Phone, ShieldCheck, KeyRound } from 'lucide-react';

export const AuthPage: React.FC = () => {
  const { login, register, isAuthenticated } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();
  const location = useLocation();

  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [submitting, setSubmitting] = useState(false);

  // Login form state
  const [loginEmail, setLoginEmail] = useState('');
  const [loginPassword, setLoginPassword] = useState('');

  // Register form state
  const [regFirstName, setRegFirstName] = useState('');
  const [regLastName, setRegLastName] = useState('');
  const [regEmail, setRegEmail] = useState('');
  const [regPassword, setRegPassword] = useState('');
  const [regPhone, setRegPhone] = useState('');

  const redirectPath = new URLSearchParams(location.search).get('redirect') || '/';

  // If already authenticated, redirect
  React.useEffect(() => {
    if (isAuthenticated) {
      navigate(redirectPath, { replace: true });
    }
  }, [isAuthenticated, navigate, redirectPath]);

  const handleLoginSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!loginEmail || !loginPassword) {
      showToast('Please enter both email and password.', 'error');
      return;
    }

    try {
      setSubmitting(true);
      await login({ email: loginEmail, password: loginPassword });
      showToast('Authenticated successfully. Welcome back to JPBazaar.', 'success');
      navigate(redirectPath, { replace: true });
    } catch (err: any) {
      const msg = err.response?.data?.message || 'Authentication failed. Please verify credentials.';
      showToast(msg, 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleRegisterSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!regFirstName || !regLastName || !regEmail || !regPassword) {
      showToast('Please complete all required fields.', 'error');
      return;
    }

    try {
      setSubmitting(true);
      await register({
        firstName: regFirstName,
        lastName: regLastName,
        email: regEmail,
        password: regPassword,
        phone: regPhone || undefined,
      });
      showToast('Account created and privileged token issued.', 'success');
      navigate(redirectPath, { replace: true });
    } catch (err: any) {
      const msg = err.response?.data?.message || 'Registration failed. Please check inputs.';
      showToast(msg, 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const fillDemoAdmin = () => {
    setLoginEmail('admin@jpbazaar.com');
    setLoginPassword('Admin@123');
    setMode('login');
  };

  const fillDemoCustomer = () => {
    setLoginEmail('customer@jpbazaar.com');
    setLoginPassword('Customer@123');
    setMode('login');
  };

  return (
    <div className="min-h-[85vh] flex items-center justify-center px-4 py-16 bg-luxury-black">
      <div className="max-w-md w-full">
        {/* Brand Header */}
        <div className="text-center mb-8">
          <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">
            Private Access Portal
          </span>
          <h1 className="font-serif text-3xl font-bold text-luxury-white mt-1">
            {mode === 'login' ? 'Client Sign In' : 'Client Registration'}
          </h1>
          <p className="text-xs text-luxury-muted mt-2">
            Seamless synchronization across our high-performance backend.
          </p>
        </div>

        {/* Auth Mode Toggle */}
        <div className="flex border-b border-luxury-border mb-6">
          <button
            type="button"
            onClick={() => setMode('login')}
            className={`flex-1 pb-3 text-sm font-semibold tracking-wider uppercase transition-colors ${
              mode === 'login'
                ? 'text-luxury-gold border-b-2 border-luxury-gold'
                : 'text-luxury-muted hover:text-luxury-white'
            }`}
          >
            Sign In
          </button>
          <button
            type="button"
            onClick={() => setMode('register')}
            className={`flex-1 pb-3 text-sm font-semibold tracking-wider uppercase transition-colors ${
              mode === 'register'
                ? 'text-luxury-gold border-b-2 border-luxury-gold'
                : 'text-luxury-muted hover:text-luxury-white'
            }`}
          >
            Create Account
          </button>
        </div>

        <Card variant="elevated" className="p-8">
          {mode === 'login' ? (
            <form onSubmit={handleLoginSubmit} className="space-y-5">
              <Input
                label="Email Address"
                type="email"
                placeholder="client@jpbazaar.com"
                value={loginEmail}
                onChange={(e) => setLoginEmail(e.target.value)}
                leftIcon={<Mail size={16} />}
                required
              />

              <Input
                label="Password"
                type="password"
                placeholder="••••••••••••"
                value={loginPassword}
                onChange={(e) => setLoginPassword(e.target.value)}
                leftIcon={<Lock size={16} />}
                required
              />

              <Button
                type="submit"
                variant="gold"
                fullWidth
                isLoading={submitting}
              >
                Sign In to JPBazaar
              </Button>
            </form>
          ) : (
            <form onSubmit={handleRegisterSubmit} className="space-y-4">
              <div className="grid grid-cols-2 gap-3">
                <Input
                  label="First Name"
                  placeholder="Alexander"
                  value={regFirstName}
                  onChange={(e) => setRegFirstName(e.target.value)}
                  leftIcon={<UserIcon size={16} />}
                  required
                />
                <Input
                  label="Last Name"
                  placeholder="Vance"
                  value={regLastName}
                  onChange={(e) => setRegLastName(e.target.value)}
                  required
                />
              </div>

              <Input
                label="Email Address"
                type="email"
                placeholder="alexander@domain.com"
                value={regEmail}
                onChange={(e) => setRegEmail(e.target.value)}
                leftIcon={<Mail size={16} />}
                required
              />

              <Input
                label="Password"
                type="password"
                placeholder="At least 8 characters"
                value={regPassword}
                onChange={(e) => setRegPassword(e.target.value)}
                leftIcon={<Lock size={16} />}
                helperText="Must include uppercase, lowercase, and number"
                required
              />

              <Input
                label="Phone Number (Optional)"
                type="tel"
                placeholder="+1 (555) 019-2834"
                value={regPhone}
                onChange={(e) => setRegPhone(e.target.value)}
                leftIcon={<Phone size={16} />}
              />

              <Button
                type="submit"
                variant="gold"
                fullWidth
                isLoading={submitting}
              >
                Register Private Account
              </Button>
            </form>
          )}

          {/* Seed Quick-Fill Credentials */}
          <div className="mt-8 pt-6 border-t border-luxury-border">
            <div className="flex items-center gap-1.5 text-xs font-semibold text-luxury-muted uppercase tracking-wider mb-3">
              <KeyRound size={13} className="text-luxury-gold" />
              <span>Instant Test Credentials</span>
            </div>
            <div className="grid grid-cols-2 gap-2">
              <button
                type="button"
                onClick={fillDemoAdmin}
                className="px-3 py-2 text-xs bg-luxury-surface hover:bg-luxury-elevated border border-luxury-border rounded-sm text-left transition-colors"
              >
                <div className="font-semibold text-luxury-white flex items-center gap-1">
                  <ShieldCheck size={12} className="text-luxury-gold" /> Admin Portal
                </div>
                <div className="text-[10px] text-luxury-muted truncate">admin@jpbazaar.com</div>
              </button>
              <button
                type="button"
                onClick={fillDemoCustomer}
                className="px-3 py-2 text-xs bg-luxury-surface hover:bg-luxury-elevated border border-luxury-border rounded-sm text-left transition-colors"
              >
                <div className="font-semibold text-luxury-white flex items-center gap-1">
                  <UserIcon size={12} className="text-luxury-gold" /> Client VIP
                </div>
                <div className="text-[10px] text-luxury-muted truncate">customer@jpbazaar.com</div>
              </button>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};
