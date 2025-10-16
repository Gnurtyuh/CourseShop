document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("registerForm");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const name = document.getElementById("username").value.trim();
        const email = document.getElementById("email").value.trim();
        const passwordHash = document.getElementById("password").value;
        const balance = 0;
        if (!name || !email || !passwordHash) {
            alert("Vui lòng điền đầy đủ thông tin.");
            return;
        }

        try {
            const res = await fetch("http://localhost:8080/CourseShop/api/public/auth", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ name, email, passwordHash,balance }),
            });

            if (res.ok) {
                alert("Đăng ký thành công! Chuyển đến trang đăng nhập...");
                window.location.href = "login.html";
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
