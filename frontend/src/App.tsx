import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard'; // Import Dashboard mới

// Khóa cửa: Nếu chưa có token thì bắt ra màn hình đăng nhập
const ProtectedRoute = ({ children }: { children: JSX.Element }) => {
    const token = localStorage.getItem('token');
    if (!token) return <Navigate to="/login" />;
    return children;
};

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />

                {/* Trang chủ sẽ trỏ thẳng vào Dashboard */}
                <Route
                    path="/"
                    element={
                        <ProtectedRoute>
                            <Dashboard />
                        </ProtectedRoute>
                    }
                />
            </Routes>
        </BrowserRouter>
    );
}

export default App;