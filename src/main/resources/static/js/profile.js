document.addEventListener("DOMContentLoaded", () => {
    const userMenu = document.getElementById("user-menu");
    const authButtons = document.getElementById("auth-buttons");
    const menuName = document.getElementById("menu-name");
    const userName = document.getElementById("user-name");
    const userEmail = document.getElementById("user-email");
    const userBalance = document.getElementById("user-balance");
    const userCreated = document.getElementById("user-created");
    const courseList = document.getElementById("course-list");
    const editProfileBtn = document.getElementById("edit-profile-btn");
    const changePasswordBtn = document.getElementById("change-password-btn");
    const editProfileModal = document.getElementById("edit-profile-modal");
    const changePasswordModal = document.getElementById("change-password-modal");
    const closeModal = document.getElementById("close-modal");
    const closePasswordModal = document.getElementById("close-password-modal");
    const editProfileForm = document.getElementById("edit-profile-form");
    const changePasswordForm = document.getElementById("change-password-form");
    const notification = document.getElementById("notification");
    const logoutBtn = document.getElementById("logout-btn");
    const myCoursesLink = document.getElementById("my-courses-link");
    const profileLink = document.getElementById("profile-link");
    const topupLink = document.getElementById("topup-link");
    const contactLink = document.getElementById("contact-link");
    const topupGuideLink = document.getElementById("topup-guide-link");
    const searchInput = document.getElementById("search-input");
    const searchBtn = document.getElementById("search-btn");

    let currentUser = null;
    const token = localStorage.getItem("userToken");

    // Hàm hiển thị thông báo
    function showNotification(message, isError = false) {
        if (!notification) return console.error("Notification element not found");
        notification.textContent = message;
        notification.className = isError ? "error" : "";
        notification.style.display = "block";
        setTimeout(() => {
            notification.style.display = "none";
        }, 3000);
    }

    // Hàm đăng xuất
    function logout() {
        localStorage.removeItem("userToken");
        localStorage.removeItem("user");
        showNotification("Bạn đã đăng xuất.");
        setTimeout(() => {
            window.location.href = "index.html";
        }, 1500);
    }

    // Hàm chuyển hướng đăng nhập
    function redirectToLogin() {
        if (authButtons) authButtons.style.display = "flex";
        if (userMenu) userMenu.style.display = "none";
        setTimeout(() => {
            window.location.href = "login.html";
        }, 1500);
    }

    // Hàm fetch danh sách khóa học
    async function fetchMyCourses() {
        try {
            const myCoursesRes = await fetch("http://localhost:8080/api/users/course/myCourse", {
                headers: {
                    "Authorization": "Bearer " + token,
                },
            });

            if (!myCoursesRes.ok) {
                const errorText = await myCoursesRes.text();
                console.warn("Lỗi lấy khóa học:", errorText);
                return;
            }

            const myCourse = await myCoursesRes.json();
            if (courseList) {
                courseList.innerHTML = "";
                const myCourses = Array.isArray(myCourse) ? myCourse : [];

                if (myCourses.length === 0) {
                    const li = document.createElement("li");
                    li.textContent = "Bạn chưa mua khóa học nào.";
                    courseList.appendChild(li);
                } else {
                    myCourses.forEach(course => {
                        const li = document.createElement("li");
                        li.textContent = course.title || "Khóa học";
                        li.style.cursor = "pointer";
                        li.onclick = () => {
                            window.location.href = `course.html?id=${course.id}`;
                        };
                        courseList.appendChild(li);
                    });
                }
            }
        } catch (err) {
            console.error("Lỗi fetch khóa học:", err);
        }
    }

    // Hàm kiểm tra đăng nhập
    async function checkLogin() {
        if (!token) {
            redirectToLogin();
            return false;
        }

        try {
            const res = await fetch("http://localhost:8080/api/users/user/me", {
                headers: {
                    "Authorization": "Bearer " + token,
                },
            });

            const responseText = await res.text();

            if (!res.ok) {
                if (responseText.includes("Token không hợp lệ") ||
                    responseText.includes("Token hết hạn") ||
                    responseText.includes("Unauthorized")) {
                    showNotification("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!", true);
                } else {
                    showNotification(responseText || "Lỗi xác thực!", true);
                }
                redirectToLogin();
                return false;
            }

            // Parse JSON nếu thành công
            try {
                const user = JSON.parse(responseText);
                currentUser = user;

                // Hiển thị giao diện người dùng
                if (authButtons) authButtons.style.display = "none";
                if (userMenu) {
                    userMenu.style.display = "flex";
                    if (menuName) menuName.textContent = user.name || "User";
                }

                // Hiển thị thông tin cá nhân
                if (userName) userName.textContent = user.name || "Chưa cập nhật";
                if (userEmail) userEmail.textContent = user.email || "Chưa cập nhật";
                if (userBalance) userBalance.textContent = (user.balance || 0).toLocaleString('vi-VN') + " VND";

                // Format ngày tạo
                if (userCreated) {
                    const createdDate = user.createdAt ? new Date(user.createdAt) : new Date();
                    userCreated.textContent = createdDate.toLocaleDateString('vi-VN');
                }

                // Lấy danh sách khóa học đã mua
                await fetchMyCourses();

                return true;
            } catch (parseError) {
                console.error("Lỗi parse JSON:", parseError);
                showNotification("Lỗi dữ liệu từ server!", true);
                redirectToLogin();
                return false;
            }

        } catch (err) {
            console.error("Lỗi khi xác thực:", err);
            showNotification("❌ Lỗi kết nối đến máy chủ!", true);
            redirectToLogin();
            return false;
        }
    }

    // Kiểm tra đăng nhập khi tải trang
    checkLogin();

    // ===== MENU HANDLING =====
    if (userMenu) {
        userMenu.onclick = (e) => {
            e.stopPropagation();
            userMenu.classList.toggle("active");
        };
    }

    document.addEventListener("click", (e) => {
        if (userMenu && !userMenu.contains(e.target)) {
            userMenu.classList.remove("active");
        }
    });

    if (userMenu) {
        const userMenuUl = userMenu.querySelector("ul");
        if (userMenuUl) {
            userMenuUl.onclick = (e) => {
                e.stopPropagation();
            };
        }
    }

    // ===== MENU LINKS =====
    if (logoutBtn) {
        logoutBtn.onclick = (e) => {
            e.preventDefault();
            userMenu.classList.remove("active");
            logout();
        };
    }

    if (myCoursesLink) {
        myCoursesLink.onclick = (e) => {
            e.preventDefault();
            userMenu.classList.remove("active");
            window.location.href = "my-courses.html";
        };
    }

    if (profileLink) {
        profileLink.onclick = (e) => {
            e.preventDefault();
            userMenu.classList.remove("active");
            window.location.href = "profile.html";
        };
    }

    if (topupLink) {
        topupLink.onclick = (e) => {
            e.preventDefault();
            userMenu.classList.remove("active");
            window.location.href = "topup.html";
        };
    }

    if (contactLink) {
        contactLink.onclick = (e) => {
            e.preventDefault();
            userMenu.classList.remove("active");
            window.location.href = "contact.html";
        };
    }

    if (topupGuideLink) {
        topupGuideLink.onclick = (e) => {
            e.preventDefault();
            userMenu.classList.remove("active");
            window.location.href = "topup-guide.html";
        };
    }

    // ===== SEARCH HANDLING =====
    if (searchBtn) {
        searchBtn.onclick = () => {
            const query = searchInput?.value.trim();
            if (query) {
                window.location.href = `index.html?search=${encodeURIComponent(query)}`;
            }
        };
    }

    if (searchInput) {
        searchInput.onkeydown = (e) => {
            if (e.key === "Enter") {
                const query = searchInput.value.trim();
                if (query) {
                    window.location.href = `index.html?search=${encodeURIComponent(query)}`;
                }
            }
        };
    }

    // ===== MODAL HANDLING =====
    // Mở modal chỉnh sửa thông tin
    if (editProfileBtn) {
        editProfileBtn.onclick = () => {
            if (editProfileModal) {
                editProfileModal.style.display = "flex";
                if (currentUser) {
                    document.getElementById("edit-name").value = currentUser.name || "";
                    document.getElementById("edit-email").value = currentUser.email || "";
                }
            }
        };
    }

    // Đóng modal chỉnh sửa
    if (closeModal) {
        closeModal.onclick = () => {
            if (editProfileModal) editProfileModal.style.display = "none";
        };
    }

    // Mở modal đổi mật khẩu
    if (changePasswordBtn) {
        changePasswordBtn.onclick = () => {
            if (changePasswordModal) {
                changePasswordModal.style.display = "flex";
                document.getElementById("current-password").value = "";
                document.getElementById("new-password").value = "";
                document.getElementById("confirm-password").value = "";
            }
        };
    }

    // Đóng modal đổi mật khẩu
    if (closePasswordModal) {
        closePasswordModal.onclick = () => {
            if (changePasswordModal) changePasswordModal.style.display = "none";
        };
    }

    // Đóng modal khi click bên ngoài
    window.onclick = (e) => {
        if (e.target === editProfileModal) {
            editProfileModal.style.display = "none";
        }
        if (e.target === changePasswordModal) {
            changePasswordModal.style.display = "none";
        }
    };

    // ===== FORM HANDLING =====
    // Xử lý form chỉnh sửa thông tin
    if (editProfileForm) {
        editProfileForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const name = document.getElementById("edit-name").value.trim();
            const email = document.getElementById("edit-email").value.trim();
            const submitBtn = editProfileForm.querySelector('button[type="submit"]');
            const originalText = submitBtn.textContent;

            if (!name || !email) {
                showNotification("Vui lòng điền đầy đủ thông tin!", true);
                return;
            }

            try {
                submitBtn.disabled = true;
                submitBtn.textContent = "Đang xử lý...";

                const response = await fetch(`http://localhost:8080/api/users/user`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`,
                    },
                    body: JSON.stringify({ name, email }),
                });

                const responseText = await response.text();

                if (!response.ok) {
                    if (response.status === 401) {
                        showNotification("❌ Tên hoặc Email này đã được sử dụng bởi tài khoản khác!", true);
                        return;
                    }
                    // Xử lý lỗi từ server
                    if (responseText.includes("email") && responseText.includes("tồn tại")) {
                        showNotification("❌ Email này đã được sử dụng bởi tài khoản khác!", true);
                    } else if (responseText.includes("tên") && responseText.includes("tồn tại")) {
                        showNotification("❌ Tên này đã được sử dụng bởi tài khoản khác!", true);
                    } else if (responseText.includes("Email") && responseText.includes("tồn tại")) {
                        showNotification("❌ Email đã tồn tại trong hệ thống!", true);
                    } else if (responseText.includes("Tên") && responseText.includes("tồn tại")) {
                        showNotification("❌ Tên đã tồn tại trong hệ thống!", true);
                    } else {
                        showNotification(responseText || "Cập nhật thất bại!", true);
                    }
                    return;
                }

                // Thành công - reload trang sau 1.5 giây
                showNotification("✅ Cập nhật thông tin thành công!");
                if (editProfileModal) editProfileModal.style.display = "none";

                // Reload trang để cập nhật dữ liệu mới
                setTimeout(() => {
                    window.location.reload();
                }, 1500);

            } catch (err) {
                console.error("Lỗi cập nhật:", err);
                showNotification("❌ Lỗi kết nối đến máy chủ!", true);
            } finally {
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;
            }
        });
    }

    // Xử lý form đổi mật khẩu
    if (changePasswordForm) {
        changePasswordForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const currentPassword = document.getElementById("current-password").value;
            const newPassword = document.getElementById("new-password").value;
            const confirmPassword = document.getElementById("confirm-password").value;
            const submitBtn = changePasswordForm.querySelector('button[type="submit"]');
            const originalText = submitBtn.textContent;

            // Kiểm tra mật khẩu mới
            if (!currentPassword || !newPassword || !confirmPassword) {
                showNotification("Vui lòng điền đầy đủ thông tin!", true);
                return;
            }

            if (newPassword.length < 6) {
                showNotification("❌ Mật khẩu mới phải có ít nhất 6 ký tự!", true);
                return;
            }

            if (newPassword !== confirmPassword) {
                showNotification("❌ Mật khẩu mới không khớp!", true);
                return;
            }

            try {
                submitBtn.disabled = true;
                submitBtn.textContent = "Đang xử lý...";

                const response = await fetch("http://localhost:8080/api/users/user/change-password", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`,
                    },
                    body: JSON.stringify({
                        currentPassword,
                        newPassword
                    }),
                });

                const responseText = await response.text();

                if (!response.ok) {
                    // Xử lý lỗi từ server
                    if (responseText.includes("Mật khẩu hiện tại không đúng")) {
                        showNotification("❌ Mật khẩu hiện tại không chính xác!", true);
                    } else if (responseText.includes("Mật khẩu mới phải khác mật khẩu cũ")) {
                        showNotification("❌ Mật khẩu mới phải khác mật khẩu hiện tại!", true);
                    } else {
                        showNotification(responseText || "Đổi mật khẩu thất bại!", true);
                    }
                    return;
                }

                // Thành công
                showNotification("✅ Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
                if (changePasswordModal) changePasswordModal.style.display = "none";

                // Đăng xuất và chuyển về trang đăng nhập sau 2 giây
                setTimeout(() => {
                    localStorage.removeItem("userToken");
                    localStorage.removeItem("user");
                    window.location.href = "login.html";
                }, 2000);

            } catch (err) {
                console.error("Lỗi đổi mật khẩu:", err);
                showNotification("❌ Lỗi kết nối đến máy chủ!", true);
            } finally {
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;
            }
        });
    }
});