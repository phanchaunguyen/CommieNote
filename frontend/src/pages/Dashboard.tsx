import { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import ReactQuill from 'react-quill-new';
import 'react-quill-new/dist/quill.snow.css';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

interface Topic {
    id: string;
    name: string;
    description: string;
    slug: string;
    createdBy: string;
}

interface Chapter {
    id: string;
    title: string;
    topicId: string;
}

interface MasterNoteResponse {
    id: string;
    chapterId: string;
    aggregatedContent: string;
    wordCount: number;
    lastAiRun: string;
}

export default function Dashboard() {
    const navigate = useNavigate();

    // States Danh sách
    const [topics, setTopics] = useState<Topic[]>([]);
    const [chapters, setChapters] = useState<Chapter[]>([]);

    // States Chọn lọc & Điều hướng
    const [activeTab, setActiveTab] = useState<'MY_TOPICS' | 'PUBLIC_TOPICS'>('MY_TOPICS');
    const [selectedTopic, setSelectedTopic] = useState<Topic | null>(null);
    const [selectedChapter, setSelectedChapter] = useState<Chapter | null>(null);

    // States Nội dung
    const [noteHtml, setNoteHtml] = useState<string>("");
    const [masterNote, setMasterNote] = useState<MasterNoteResponse | null>(null);

    // States Loading & Modals
    const [isLoadingTopics, setIsLoadingTopics] = useState(false);
    const [isMasterNoteLoading, setIsMasterNoteLoading] = useState(false);
    const [isEditorLoading, setIsEditorLoading] = useState(false);

    const [showTopicModal, setShowTopicModal] = useState(false);
    const [newTopicName, setNewTopicName] = useState('');
    const [newTopicDesc, setNewTopicDesc] = useState('');
    const [showChapterModal, setShowChapterModal] = useState(false);
    const [newChapterTitle, setNewChapterTitle] = useState('');

    // Tối ưu hóa Toolbar bằng useMemo
    const modules = useMemo(() => ({
        toolbar: [
            [{ 'header': [1, 2, 3, false] }],
            ['bold', 'italic', 'underline', 'strike'],
            [{ 'list': 'ordered'}, { 'list': 'bullet' }],
            [{ 'color': [] }, { 'background': [] }],
            ['clean']
        ],
    }), []);

    //  API
    const getToken = () => localStorage.getItem('token');

    const fetchTopics = async (isPublic: boolean) => {
        setIsLoadingTopics(true);
        try {
            const endpoint = isPublic ? `${API_BASE_URL}/api/topics/public` : `${API_BASE_URL}/api/topics`;
            const res = await fetch(endpoint, { headers: { 'Authorization': `Bearer ${getToken()}` } });
            if (res.status === 401) return handleLogout();
            if (res.ok) setTopics(await res.json());
        } catch (err) { console.error(err); }
        finally { setIsLoadingTopics(false); }
    };

    const fetchChapters = async (topicId: string) => {
        try {
            const res = await fetch(`${API_BASE_URL}/api/chapters/topic/${topicId}`, { headers: { 'Authorization': `Bearer ${getToken()}` } });
            if (res.ok) setChapters(await res.json());
        } catch (err) { console.error(err); }
    };

    // Lấy Note của User
    const fetchUserNote = async (chapterId: string) => {
        setIsEditorLoading(true);
        try {
            const res = await fetch(`${API_BASE_URL}/api/notes/chapter/${chapterId}`, { headers: { 'Authorization': `Bearer ${getToken()}` } });
            if (res.status === 204) {
                setNoteHtml("<h2>Bắt đầu viết Note của bạn...</h2><p><br></p>"); // Mẫu mặc định
            } else if (res.ok) {
                const data = await res.json();
                setNoteHtml(data.content);
            }
        } catch (err) { console.error(err); }
        finally { setIsEditorLoading(false); }
    };

    // Lấy Master Note
    const fetchMasterNote = async (chapterId: string) => {
        setIsMasterNoteLoading(true);
        try {
            const res = await fetch(`${API_BASE_URL}/api/masternotes/chapter/${chapterId}`, { headers: { 'Authorization': `Bearer ${getToken()}` } });
            if (res.status === 204) {
                setMasterNote(null);
            } else if (res.ok) {
                setMasterNote(await res.json());
            }
        } catch (err) { console.error(err); }
        finally { setIsMasterNoteLoading(false); }
    };

    // Tạo & Lưu dữ liệu
    const handleCreateTopic = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const res = await fetch(`${API_BASE_URL}/api/topics`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${getToken()}`, 'Content-Type': 'application/json' },
                body: JSON.stringify({ name: newTopicName, description: newTopicDesc })
            });
            if (res.ok) {
                setShowTopicModal(false); setNewTopicName(''); setNewTopicDesc('');
                fetchTopics(activeTab === 'PUBLIC_TOPICS');
            }
        } catch (err) { console.error(err); }
    };

    const handleCreateChapter = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!selectedTopic) return;
        try {
            const res = await fetch(`${API_BASE_URL}/api/chapters`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${getToken()}`, 'Content-Type': 'application/json' },
                body: JSON.stringify({ topicId: selectedTopic.id, title: newChapterTitle })
            });
            if (res.ok) {
                setShowChapterModal(false); setNewChapterTitle('');
                fetchChapters(selectedTopic.id);
            }
        } catch (err) { console.error(err); }
    };

    const handleSaveNote = async () => {
        if (!selectedChapter) return alert("Vui lòng chọn chapter trước!");
        try {
            const res = await fetch(`${API_BASE_URL}/api/notes`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${getToken()}`, 'Content-Type': 'application/json' },
                body: JSON.stringify({ chapterId: selectedChapter.id, content: noteHtml })
            });
            if (res.ok) alert("✅ Đã lưu Note thành công!");
        } catch (err) { console.error(err); alert("Lỗi khi lưu note!"); }
    };

    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    // EFFECTS (Cơ chế đồng bộ UI)
    useEffect(() => {
        fetchTopics(activeTab === 'PUBLIC_TOPICS');
        setSelectedTopic(null); setSelectedChapter(null);
    }, [activeTab]);

    useEffect(() => {
        if (selectedTopic) {
            fetchChapters(selectedTopic.id);
            setSelectedChapter(null);
        }
    }, [selectedTopic]);

    // KHI CLICK VÀO 1 CHAPTER -> GỌI 2 API TẢI DỮ LIỆU
    useEffect(() => {
        if (selectedChapter) {
            fetchUserNote(selectedChapter.id);
            fetchMasterNote(selectedChapter.id);
        }
    }, [selectedChapter]);


    // UI
    return (
        <div className="h-screen w-screen flex bg-slate-50 overflow-hidden font-sans text-slate-800">

            {/* ================= CỘT 1: TOPICS (Dark Mode Sát Cạnh) ================= */}
            <div className={`h-full flex flex-col bg-slate-900 text-slate-300 transition-all duration-300 shadow-2xl z-20 ${selectedChapter ? 'w-20 items-center' : 'w-72'}`}>

                {/* Logo / Header */}
                <div className="p-5 border-b border-slate-800 w-full flex justify-center items-center h-20">
                    {selectedChapter ? (
                        <span className="font-black text-2xl tracking-tighter text-indigo-500">CN</span>
                    ) : (
                        <>
                            <img src="/commienote.svg" className="h-8 w-auto mr-2" alt="Brand logo" />
                            <h1 className="font-black text-2xl tracking-tight text-white flex items-center">
                                <span className="text-indigo-500">Commie</span>Note
                            </h1>
                        </>
                    )}
                </div>

                <div className="p-4 flex-1 flex flex-col w-full overflow-hidden">
                    {!selectedChapter ? (
                        <>
                            {/* Nút Tạo Mới */}
                            <button onClick={() => setShowTopicModal(true)} className="w-full bg-indigo-600 hover:bg-indigo-500 text-white font-semibold py-2.5 rounded-lg mb-6 transition shadow-lg shadow-indigo-500/30">
                                + New Topic
                            </button>

                            {/* Toggle Tab phong cách iOS */}
                            <div className="flex mb-6 bg-slate-800/50 p-1 rounded-lg border border-slate-700/50">
                                <button onClick={() => setActiveTab('MY_TOPICS')} className={`flex-1 text-xs py-1.5 font-medium rounded-md transition ${activeTab === 'MY_TOPICS' ? 'bg-slate-700 text-white shadow' : 'text-slate-400 hover:text-slate-200'}`}>
                                    My Topics
                                </button>
                                <button onClick={() => setActiveTab('PUBLIC_TOPICS')} className={`flex-1 text-xs py-1.5 font-medium rounded-md transition ${activeTab === 'PUBLIC_TOPICS' ? 'bg-slate-700 text-white shadow' : 'text-slate-400 hover:text-slate-200'}`}>
                                    Public
                                </button>
                            </div>

                            {/* Danh sách Topic */}
                            <div className="space-y-1 overflow-y-auto flex-1 custom-scrollbar pr-2">
                                {isLoadingTopics && <p className="text-sm italic text-slate-500 text-center mt-4">Loading...</p>}
                                {!isLoadingTopics && topics.length === 0 && <p className="text-sm italic text-slate-500 text-center mt-4">No topics found.</p>}

                                {topics.map((topic) => (
                                    <div
                                        key={topic.id} onClick={() => setSelectedTopic(topic)}
                                        className={`p-3 rounded-xl cursor-pointer transition flex flex-col gap-1 border ${selectedTopic?.id === topic.id ? 'bg-slate-800 border-indigo-500/50 text-white' : 'border-transparent hover:bg-slate-800/50'}`}
                                    >
                                        <span className="font-semibold truncate text-sm">📁 {topic.name}</span>
                                        {activeTab === 'PUBLIC_TOPICS' && <span className="text-[10px] text-slate-500 truncate">By @{topic.createdBy}</span>}
                                    </div>
                                ))}
                            </div>
                        </>
                    ) : (
                        // Nút mở rộng lại menu
                        <div className="flex flex-col gap-4 mt-4 w-full px-2">
                            <button onClick={() => setSelectedChapter(null)} className="w-full bg-slate-800 hover:bg-slate-700 p-3 rounded-xl flex justify-center text-slate-400 hover:text-white transition" title="Expand Menu">
                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 5l7 7-7 7M5 5l7 7-7 7"></path></svg>
                            </button>
                        </div>
                    )}
                </div>

                {/* Nút Đăng xuất ở đáy */}
                <div className="p-4 border-t border-slate-800 w-full">
                    <button onClick={handleLogout} className="w-full flex justify-center items-center text-slate-400 hover:text-red-400 font-medium py-2 rounded-lg hover:bg-slate-800 transition">
                        {!selectedChapter ? 'Sign Out' : '🚪'}
                    </button>
                </div>
            </div>

            {/* ================= CỘT 2: CHAPTERS (Chỉ hiện khi chọn Topic) ================= */}
            {selectedTopic && (
                <div className="h-full w-72 bg-white border-r border-slate-200 flex flex-col shadow-sm z-10">
                    <div className="p-5 border-b border-slate-100 bg-slate-50/50 h-20 flex flex-col justify-center">
                        <h2 className="font-bold text-slate-800 truncate text-lg">{selectedTopic.name}</h2>
                        <p className="text-xs text-slate-500 truncate">{selectedTopic.description || 'No description'}</p>
                    </div>

                    <div className="p-4 flex-1 flex flex-col overflow-hidden">
                        {activeTab === 'MY_TOPICS' && (
                            <button onClick={() => setShowChapterModal(true)} className="w-full bg-slate-50 hover:bg-indigo-50 border border-dashed border-slate-300 hover:border-indigo-300 text-slate-600 hover:text-indigo-600 font-medium py-2 rounded-lg mb-4 text-sm transition">
                                + Add New Chapter
                            </button>
                        )}

                        <div className="space-y-1 overflow-y-auto flex-1 custom-scrollbar">
                            {chapters.length === 0 && <p className="text-sm italic text-slate-400 text-center mt-4">Empty chapter list.</p>}
                            {chapters.map((chapter, index) => (
                                <div
                                    key={chapter.id} onClick={() => setSelectedChapter(chapter)}
                                    className={`p-3 rounded-lg cursor-pointer transition text-sm font-medium flex items-center gap-3 ${selectedChapter?.id === chapter.id ? 'bg-indigo-50 text-indigo-700' : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'}`}
                                >
                                    <span className="w-6 h-6 rounded bg-white shadow-sm border border-slate-100 flex items-center justify-center text-[10px] font-bold text-slate-400">{index + 1}</span>
                                    <span className="truncate">{chapter.title}</span>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            )}

            {/* ================= CỘT 3 & 4: KHU VỰC LÀM VIỆC ================= */}
            {selectedChapter ? (
                <div className="h-full flex-1 flex bg-white">

                    {/* CỘT 3: EDITOR */}
                    <div className="h-full flex-1 flex flex-col border-r border-slate-200 min-w-[50%]">
                        {/* Toolbar trên cùng của Editor */}
                        <div className="px-8 py-4 border-b border-slate-100 flex justify-between items-center bg-white h-20 shrink-0">
                            <div className="flex items-center gap-4">
                                <div className="w-10 h-10 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-600 font-bold">
                                    📝
                                </div>
                                <div>
                                    <h2 className="text-lg font-bold text-slate-800">{selectedChapter.title}</h2>
                                </div>
                            </div>
                            <button onClick={handleSaveNote} className="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-2.5 rounded-lg font-medium transition shadow-md shadow-indigo-200 flex items-center gap-2 text-sm">
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 7H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-3m-1 4l-3 3m0 0l-3-3m3 3V4"></path></svg>
                                Save Draft
                            </button>
                        </div>

                        {/* Vùng soạn thảo */}
                        <div className="flex-1 overflow-hidden flex flex-col relative">
                            {isEditorLoading ? (
                                <div className="absolute inset-0 bg-white/80 z-10 flex items-center justify-center text-slate-400">Đang tải Note cũ...</div>
                            ) : null}

                            {/* CSS bọc Quill để nó tràn viền đẹp hơn */}
                            <div className="h-full w-full [&_.ql-toolbar]:border-none [&_.ql-toolbar]:border-b [&_.ql-toolbar]:border-slate-200 [&_.ql-toolbar]:bg-slate-50 [&_.ql-toolbar]:px-8 [&_.ql-toolbar]:py-3 [&_.ql-container]:border-none [&_.ql-editor]:px-8 [&_.ql-editor]:py-6 [&_.ql-editor]:text-slate-700 [&_.ql-editor]:text-base">
                                <ReactQuill theme="snow" value={noteHtml} onChange={setNoteHtml} modules={modules} className="h-full flex flex-col pb-10" />
                            </div>
                        </div>
                    </div>

                    {/* CỘT 4: MASTER NOTE (Bảng AI Tổng hợp) */}
                    <div className="h-full w-[400px] xl:w-[450px] bg-slate-50 flex flex-col shrink-0">
                        <div className="px-6 py-4 border-b border-slate-200 bg-slate-100/50 h-20 flex items-center shrink-0">
                            <h2 className="text-lg font-bold text-slate-800 flex items-center gap-2">
                                <span className="text-indigo-500">Master Note</span>
                            </h2>
                        </div>

                        <div className="p-6 overflow-y-auto flex-1 custom-scrollbar">
                            {isMasterNoteLoading ? (
                                <div className="animate-pulse space-y-6">
                                    <div className="h-4 bg-slate-200 rounded w-1/3"></div>
                                    <div className="space-y-3"><div className="h-3 bg-slate-200 rounded"></div><div className="h-3 bg-slate-200 rounded w-5/6"></div><div className="h-3 bg-slate-200 rounded w-4/6"></div></div>
                                </div>
                            ) : !masterNote ? (
                                <div className="bg-white border border-slate-200 rounded-2xl p-8 text-center shadow-sm">
                                    <div className="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-4 text-2xl">🤖</div>
                                    <h3 className="text-slate-700 font-bold mb-2">Chưa có dữ liệu tổng hợp</h3>
                                    <p className="text-sm text-slate-500 leading-relaxed">Hãy lưu thêm ghi chú của bạn. AI sẽ tự động phân tích và tạo Master Note khi có đủ dữ liệu từ mọi người.</p>
                                </div>
                            ) : (
                                <div className="bg-white border border-slate-200 rounded-2xl p-6 shadow-sm">
                                    <div className="flex justify-between items-center mb-6 border-b border-slate-100 pb-4">
                                        <span className="text-xs font-semibold text-indigo-600 bg-indigo-50 px-2.5 py-1 rounded-full">{masterNote.wordCount} words</span>
                                        <span className="text-xs text-slate-400">Updated: {new Date(masterNote.lastAiRun).toLocaleDateString()}</span>
                                    </div>

                                    {/* In ra nội dung AI - Dùng dangerouslySetInnerHTML vì MasterNote có thể trả về HTML từ AI */}
                                    <div
                                        className="prose prose-sm prose-slate max-w-none prose-headings:text-slate-800 prose-a:text-indigo-600"
                                        dangerouslySetInnerHTML={{ __html: masterNote.aggregatedContent }}
                                    />
                                </div>
                            )}
                        </div>
                    </div>

                </div>
            ) : (
                /* Màn hình trống khi chưa chọn Chapter */
                <div className="h-full flex-1 flex flex-col items-center justify-center bg-white text-slate-400 border-l border-slate-100">
                    <div className="w-24 h-24 bg-slate-50 rounded-full flex items-center justify-center mb-6 text-4xl shadow-inner">📚</div>
                    <h1 className="text-2xl font-bold text-slate-700 mb-2">Select a Chapter</h1>
                    <p className="text-slate-500">Choose a chapter from the sidebar to start writing notes.</p>
                </div>
            )}

            {/* ================= MODALS TẠO DỮ LIỆU ================= */}
            {/* Modal Tạo Topic */}
            {showTopicModal && (
                <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50">
                    <form onSubmit={handleCreateTopic} className="bg-white p-6 rounded-2xl shadow-xl w-[400px] border border-slate-100">
                        <h2 className="text-xl font-bold text-slate-800 mb-5">Create New Topic</h2>
                        <div className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-slate-600 mb-1.5">Topic Name</label>
                                <input required autoFocus placeholder="e.g. Advanced React Patterns" className="w-full border border-slate-200 p-2.5 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition" value={newTopicName} onChange={e => setNewTopicName(e.target.value)} />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-slate-600 mb-1.5">Description</label>
                                <textarea placeholder="Briefly describe what this topic is about..." className="w-full border border-slate-200 p-2.5 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition h-24 resize-none" value={newTopicDesc} onChange={e => setNewTopicDesc(e.target.value)} />
                            </div>
                        </div>
                        <div className="flex justify-end gap-3 mt-8">
                            <button type="button" onClick={() => setShowTopicModal(false)} className="px-5 py-2.5 text-slate-600 font-medium hover:bg-slate-50 rounded-xl transition">Cancel</button>
                            <button type="submit" className="px-5 py-2.5 text-white bg-indigo-600 hover:bg-indigo-700 font-medium rounded-xl transition shadow-md shadow-indigo-200">Create Topic</button>
                        </div>
                    </form>
                </div>
            )}

            {/* Modal Tạo Chapter */}
            {showChapterModal && (
                <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50">
                    <form onSubmit={handleCreateChapter} className="bg-white p-6 rounded-2xl shadow-xl w-[400px] border border-slate-100">
                        <h2 className="text-xl font-bold text-slate-800 mb-5">Add New Chapter</h2>
                        <div>
                            <label className="block text-sm font-medium text-slate-600 mb-1.5">Chapter Title</label>
                            <input required autoFocus placeholder="e.g. Introduction to Hooks" className="w-full border border-slate-200 p-2.5 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition" value={newChapterTitle} onChange={e => setNewChapterTitle(e.target.value)} />
                        </div>
                        <div className="flex justify-end gap-3 mt-8">
                            <button type="button" onClick={() => setShowChapterModal(false)} className="px-5 py-2.5 text-slate-600 font-medium hover:bg-slate-50 rounded-xl transition">Cancel</button>
                            <button type="submit" className="px-5 py-2.5 text-white bg-indigo-600 hover:bg-indigo-700 font-medium rounded-xl transition shadow-md shadow-indigo-200">Save Chapter</button>
                        </div>
                    </form>
                </div>
            )}
        </div>
    );
}