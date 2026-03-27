const BASE_URL = "/auth";

function resetForms() {
    const inputs = document.querySelectorAll('input');
    inputs.forEach(input => {
        input.value = "";
        input.style.borderColor = "rgba(255, 255, 255, 0.1)"; 
    });

    const messages = document.querySelectorAll('.msg');
    messages.forEach(msg => {
        msg.innerText = "";
    });
}

function showRegister() {
    resetForms();
    document.getElementById('login-form').classList.remove('active');
    document.getElementById('register-form').classList.add('active');
}

function showLogin() {
    resetForms();
    document.getElementById('register-form').classList.remove('active');
    document.getElementById('login-form').classList.add('active');
}

function displayMessage(elementId, text, isError) {
    const el = document.getElementById(elementId);
    el.innerText = text;
    el.style.color = isError ? "#ff4d4d" : "#4caf50";
}

async function demoLogin() {
    const username = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value;
    const msg = "login-msg";

    if (!username || !password) {
        displayMessage(msg, "Vui lòng nhập đầy đủ!", true);
        return;
    }

    try {
        const response = await fetch(`${BASE_URL}/login`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({username, password})
        });

        const result = await response.json();

        if (response.ok && result.authenticated === true) {
            localStorage.setItem('token', result.token); 
            displayMessage(msg, "Đăng nhập thành công!", false);
            
            setTimeout(() => {
                window.location.href = "/index"; 
            }, 800);
        } else {
            displayMessage(msg, "Sai tài khoản hoặc mật khẩu!", true);
        }
    } catch (error) {
        displayMessage(msg, "Lỗi kết nối Server!", true);
    }
}

// 5. Xử lý Đăng ký
async function demoRegister() {
    const fullName = document.getElementById('reg-fullname').value.trim();
    const email = document.getElementById('reg-email').value.trim();
    const username = document.getElementById('reg-username').value.trim();
    const password = document.getElementById('reg-password').value;
    const confirmPassword = document.getElementById('reg-confirm').value;
    const msg = "reg-msg";

    if (password !== confirmPassword) {
        displayMessage(msg, "Mật khẩu không khớp!", true);
        return;
    }

    const registerData = {
        username: username,
        password: password,
        confirmPassword: confirmPassword,
        fullName: fullName,
        email: email
    };

    try {
        const response = await fetch(`${BASE_URL}/register`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(registerData)
        });

        // BƯỚC 1: Parse dữ liệu JSON trả về từ Backend (chứa token và message)
        const result = await response.json();

        if (response.ok) {
            displayMessage(msg, "Đăng ký thành công! Đang tự động đăng nhập...", false);

            // BƯỚC 2: Lưu Token vào localStorage (Đây chính là hành động Auto-login)
            if (result.token) {
                localStorage.setItem('token', result.token);
            }

            // BƯỚC 3: Chuyển thẳng người dùng sang trang chủ (hoặc trang khảo sát phim)
            setTimeout(() => {
                // Thay "/index" bằng "/survey" nếu bạn làm trang khảo sát ngay sau đây
                window.location.href = "/survey";
            }, 1000);

        } else {
            // Nếu lỗi (ví dụ backend ném ra RuntimeException), hiển thị lỗi đó
            displayMessage(msg, result.message || "Tên người dùng hoặc Email đã tồn tại!", true);
        }
    } catch (error) {
        displayMessage(msg, "Lỗi hệ thống!", true);
    }
}