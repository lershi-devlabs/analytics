import { RegisterForm } from "@/components/auth/registerForm";

export default function RegisterPage() {
  return <RegisterForm onShowLogin={() => { window.location.href = '/login'; }} />;
} 