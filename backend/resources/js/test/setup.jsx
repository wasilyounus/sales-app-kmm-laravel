import '@testing-library/jest-dom';
import { cleanup } from '@testing-library/react';
import { afterEach, vi } from 'vitest';

// Cleanup after each test case (e.g. clearing jsdom)
afterEach(() => {
  cleanup();
});

// Mock route helper
global.route = vi.fn(() => ({
  params: {},
  url: '/'
}));

// Mock Lucide React icons
vi.mock('lucide-react', () => ({
  AlertTriangle: () => <div data-testid="alert-triangle-icon" />,
  Search: () => <div data-testid="search-icon" />,
  Plus: () => <div data-testid="plus-icon" />,
  X: () => <div data-testid="x-icon" />,
  ChevronDown: () => <div data-testid="chevron-down-icon" />,
  Check: () => <div data-testid="check-icon" />,
}));

// Mock shadcn UI components
vi.mock('@/Components/ui/input', () => ({
  Input: ({ ...props }) => <input {...props} />
}));

vi.mock('@/Components/ui/label', () => ({
  Label: ({ children, ...props }) => <label {...props}>{children}</label>
}));

vi.mock('@/Components/ui/button', () => ({
  Button: ({ children, ...props }) => <button {...props}>{children}</button>
}));

vi.mock('@/Components/ui/textarea', () => ({
  Textarea: ({ ...props }) => <textarea {...props} />
}));

vi.mock('@/Components/ui/checkbox', () => ({
  Checkbox: ({ ...props }) => <input type="checkbox" {...props} />
}));

vi.mock('@/Components/ui/select', () => ({
  Select: ({ children }) => <div>{children}</div>,
  SelectContent: ({ children }) => <div>{children}</div>,
  SelectItem: ({ children, ...props }) => <option {...props}>{children}</option>,
  SelectTrigger: ({ children }) => <div>{children}</div>,
  SelectValue: ({ placeholder }) => <span>{placeholder}</span>,
}));

vi.mock('@/Components/ui/dialog', () => ({
  Dialog: ({ children, open }) => open ? <div>{children}</div> : null,
  DialogContent: ({ children }) => <div>{children}</div>,
  DialogHeader: ({ children }) => <div>{children}</div>,
  DialogTitle: ({ children }) => <h2>{children}</h2>,
}));

// Mock custom components
vi.mock('@/Components/Modal', () => ({
  default: ({ show, children, onClose }) => show ? <div>{children}</div> : null
}));

vi.mock('@/Components/PageHeader', () => ({
  default: ({ title, children }) => <div><h1>{title}</h1>{children}</div>
}));

vi.mock('@/Components/Toast', () => ({
  default: ({ message }) => message ? <div>{message}</div> : null
}));

// Mock Inertia
vi.mock('@inertiajs/react', async () => {
  const actual = await vi.importActual('@inertiajs/react');
  return {
    ...actual,
    router: {
      delete: vi.fn(),
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
    },
    usePage: () => ({
      props: {
        auth: {
          user: { id: 1, name: 'Test User' }
        },
        errors: {},
        flash: {}
      },
      url: '/'
    }),
    useForm: (initialValues) => ({
      data: initialValues,
      setData: vi.fn((key, value) => {
        // Simple mock that allows chained setData calls
        if (typeof key === 'object') {
          Object.assign(initialValues, key);
        } else {
          initialValues[key] = value;
        }
      }),
      post: vi.fn(),
      put: vi.fn(),
      processing: false,
      errors: {},
      reset: vi.fn(),
      clearErrors: vi.fn(),
    }),
    Link: ({ children, ...props }) => <a {...props}>{children}</a>,
    Head: ({ children }) => <>{children}</>,
  };
});

