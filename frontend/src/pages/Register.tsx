import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';

export default function Register() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState({ text: '', type: '' });
    const navigate = useNavigate();

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        setMessage({ text: '', type: '' });

        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password }),
            });

            if (!response.ok) {
                throw new Error('Tên đăng nhập đã tồn tại hoặc lỗi server!');
            }

            setMessage({ text: 'Đăng ký thành công! Đang chuyển hướng...', type: 'success' });

            // Chờ 1.5 giây rồi chuyển về trang đăng nhập
            setTimeout(() => navigate('/login'), 1500);
        } catch (err: any) {
            setMessage({ text: err.message, type: 'error' });
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
            <div className="max-w-md w-full bg-white rounded-2xl shadow-xl p-8">
                <h2 className="text-3xl font-bold text-center text-gray-800 mb-8">Gia nhập CommieNote</h2>

                {message.text && (
                    <div className={`p-3 rounded-lg mb-4 text-sm text-center ${message.type === 'error' ? 'bg-red-100 text-red-600' : 'bg-green-100 text-green-600'}`}>
                        {message.text}
                    </div>
                )}

                <form onSubmit={handleRegister} className="space-y-6">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Tên đăng nhập mới</label>
                        <input
                            type="text"
                            required
                            className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none transition-all"
                            placeholder="Chọn một cái tên ngầu ngầu..."
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Mật khẩu bí mật</label>
                        <input
                            type="password"
                            required
                            className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none transition-all"
                            placeholder="••••••••"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </div>

                    <button
                        type="submit"
                        className="w-full bg-green-600 hover:bg-green-700 text-white font-bold py-3 px-4 rounded-lg transition-colors"
                    >
                        Tạo tài khoản
                    </button>
                </form>

                <p className="mt-6 text-center text-gray-600">
                    Đã là thành viên?{' '}
                    <Link to="/login" className="text-green-600 hover:underline font-semibold">
                        Đăng nhập
                    </Link>
                </p>
            </div>
        </div>
    );
}