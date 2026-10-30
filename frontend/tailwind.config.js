/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        luxury: {
          black: '#080808',
          secondary: '#111111',
          surface: '#181818',
          elevated: '#222222',
          border: '#2a2a2a',
          'border-light': '#383838',
          gold: '#D4AF37',
          'gold-light': '#F5D76E',
          'gold-muted': '#A88932',
          'gold-dark': '#85691C',
          ivory: '#F5F3ED',
          'ivory-dark': '#E8E5DD',
          white: '#FFFFFF',
          muted: '#A3A3A3',
          subtle: '#737373',
          success: '#10B981',
          danger: '#EF4444',
          warning: '#F59E0B',
          info: '#3B82F6',
        },
      },
      fontFamily: {
        serif: ['"Playfair Display"', 'Georgia', 'serif'],
        sans: ['"Plus Jakarta Sans"', '-apple-system', 'BlinkMacSystemFont', 'sans-serif'],
      },
      boxShadow: {
        'luxury-sm': '0 2px 8px -2px rgba(0, 0, 0, 0.5)',
        'luxury-md': '0 8px 24px -6px rgba(0, 0, 0, 0.6)',
        'luxury-lg': '0 16px 40px -10px rgba(0, 0, 0, 0.7)',
        'gold-glow': '0 0 20px rgba(212, 175, 55, 0.25)',
        'gold-glow-lg': '0 0 35px rgba(212, 175, 55, 0.35)',
      },
      letterSpacing: {
        'luxury-wide': '0.15em',
        'luxury-widest': '0.25em',
      },
      animation: {
        'fade-in': 'fadeIn 0.3s cubic-bezier(0.16, 1, 0.3, 1)',
        'slide-up': 'slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1)',
        'slide-down': 'slideDown 0.4s cubic-bezier(0.16, 1, 0.3, 1)',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { transform: 'translateY(16px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
        slideDown: {
          '0%': { transform: 'translateY(-16px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
      },
    },
  },
  plugins: [],
}
