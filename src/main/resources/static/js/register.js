document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("registerForm");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const name = document.getElementById("name").value.trim();
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value.trim();

        if (/\s/.test(name)) {
            alert("Tên không được chứa khoảng trắng");
            return false;
        }

        if (/\s/.test(email)) {
            alert("Email không được chứa khoảng trắng");
            return false;
        }
        if (password.length < 6) {
            alert("Password quá ngắn");
            return false;
        }
        if (/\s/.test(password)) {
            alert("Password không được chứa khoảng trắng");
            return false;
        }
        if (!name || !email || !password) {
            alert("Vui lòng điền đầy đủ thông tin.");
            return;
        }

        try {
            const res = await fetch("http://localhost:8080/api/public/auth", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ name, email, password }),
            });

            if (res.ok) {
                alert("Đăng ký thành công! Chuyển đến trang đăng nhập...");
                window.location.href = "login";
            } else {
                const error = await res.json();
                alert("Đăng ký thất bại: " + (error.message || "Đã xảy ra lỗi"));
            }
        } catch (err) {
            console.error("Lỗi kết nối:", err);
            alert("Không thể kết nối đến máy chủ.");
        }
    });
});
