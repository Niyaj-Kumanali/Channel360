import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { Toaster } from 'react-hot-toast'
import { ThemeProvider } from '@/app/providers/ThemeProvider'
import App from './app/App'
import './index.css'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
      staleTime: 5 * 60 * 1000,
    },
  },
})

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <ThemeProvider>
          <App />
          <Toaster
            position="bottom-left"
            gutter={10}
            containerStyle={{ bottom: 24 }}
            toastOptions={{
              duration: 4000,
              style: {
                background: 'hsl(var(--background))',
                color: 'hsl(var(--foreground))',
                borderRadius: '0.75rem',
                boxShadow: '0 4px 16px rgba(0,0,0,0.08), 0 1px 2px rgba(0,0,0,0.06)',
                padding: '10px 14px',
                fontSize: '14px',
                fontWeight: 500,
                lineHeight: '1.4',
                border: '1px solid hsl(var(--border))',
                maxWidth: '400px',
              },
              success: {
                iconTheme: { primary: 'hsl(38 92% 50%)', secondary: 'hsl(var(--background))' },
                style: {
                  borderLeft: '4px solid hsl(38 92% 50%)',
                },
              },
              error: {
                iconTheme: { primary: 'hsl(0 84.2% 60.2%)', secondary: 'hsl(var(--background))' },
                style: {
                  borderLeft: '4px solid hsl(0 84.2% 60.2%)',
                },
              },
            }}
          />
        </ThemeProvider>
      </BrowserRouter>
    </QueryClientProvider>
  </React.StrictMode>,
)
