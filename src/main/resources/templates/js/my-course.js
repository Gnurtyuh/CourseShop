document.addEventListener("DOMContentLoaded", async () => {
    const userMenu = document.getElementById("user-menu");
    const authButtons = document.getElementById("auth-buttons");
    const menuName = document.getElementById("user-name");
    const myCoursesList = document.getElementById("my-courses-list");
    const noCoursesMsg = document.getElementById("no-courses");
    const notification = document.getElementById("notification");
    const logoutBtn = document.getElementById("logout-btn");
    const myCoursesLink = document.getElementById("my-courses-link");
    const profileLink = document.getElementById("profile-link");
    const topupLink = document.getElementById("topup-link");
    const contactLink = document.getElementById("contact-link");
    const topupGuideLink = document.getElementById("topup-guide-link");
    const searchInput = document.getElementById("search-input");
    const searchBtn = document.getElementById("search-btn");

    function showNotification(message, isError = false) {
        if (!notification) return console.error("Notification element not found");
        notification.textContent = message;
        notification.className = isError ? "error" : "";
        notification.style.display = "block";
        setTimeout(() => {
            notification.style.display = "none";
        }, 3000);
    }

    const token = localStorage.getItem("userToken");

    async function checkLogin() {
        if (!token) {
            redirectToLogin();
            return null;
        }
        try {
            const res = await fetch("http://localhost:8080/CourseShop/api/users/user/me", {
                headers: {
                    Authorization: "Bearer " + token,
                },
            });
            if (!res.ok) throw new Error("Token không hợp lệ");
            const user = await res.json();
            if (user && token) {
                if (authButtons) authButtons.style.display = "none";
                if (userMenu) {
                    userMenu.style.display = "flex";
                    if (menuName) menuName.textContent = user.name || "User";
                }
                return { user, token };
            }
        } catch (err) {
            console.error(err);
            showNotification("Vui lòng đăng nhập!", true);
            redirectToLogin();
            return null;
        }
    }

    function redirectToLogin() {
        setTimeout(() => {
            window.location.href = "login.html";
        }, 1500);
    }

    async function fetchMyCourses(token) {
        try {
            const res = await fetch(`http://localhost:8080/CourseShop/api/users/course/myCourse`, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            if (!res.ok) throw new Error("Không thể lấy danh sách khóa học");
            return await res.json();
        } catch (error) {
            console.error("Lỗi khi lấy khóa học:", error);
            showNotification("Lỗi khi tải danh sách khóa học", true);
            return [];
        }
    }

    // Kiểm tra đăng nhập
    const loginResult = await checkLogin();
    if (!loginResult) return;

    function logout() {
        localStorage.removeItem("userToken");
        showNotification("Bạn đã đăng xuất.");
        setTimeout(() => {
            window.location.href = "index.html";
        }, 1500);
    }

    function goToCourseDetail(courseId) {
        window.location.href = `course.html?id=${courseId}`;
    }

    function goToCourseVideo(courseId) {
        window.location.href = `course-video.html?courseId=${courseId}`;
    }

    function renderMyCourses(courses) {
        if (!myCoursesList || !noCoursesMsg) return;

        if (!courses || courses.length === 0) {
            myCoursesList.style.display = "none";
            noCoursesMsg.style.display = "block";
            return;
        }

        noCoursesMsg.style.display = "none";
        myCoursesList.style.display = "grid";
        myCoursesList.innerHTML = "";

        courses.forEach(course => {
            const card = document.createElement("div");
            card.className = "course-card";

            // Xử lý tiêu đề
            const title = course.title || "Khóa học";

            // Xử lý mô tả
            const description = course.description || "Khóa học giúp bạn nâng cao kỹ năng và phát triển bản thân. Nội dung được thiết kế bài bản, dễ hiểu và thực tế.";

            // Tạo phần tử h3 cho tiêu đề
            const titleEl = document.createElement("h3");
            titleEl.textContent = title;

            // Tạo phần tử p cho mô tả
            const descEl = document.createElement("p");
            descEl.className = "course-description";
            descEl.textContent = description;

            // Tạo button group
            const buttonGroup = document.createElement("div");
            buttonGroup.className = "button-group";

            // Nút xem chi tiết
            const btnDetail = document.createElement("button");
            btnDetail.type = "button";
            btnDetail.className = "btn-detail";
            btnDetail.innerHTML = '<i class="fa-solid fa-circle-info"></i> Chi tiết';
            btnDetail.addEventListener("click", () => goToCourseDetail(course.id));

            // Nút xem khóa học
            const btnWatch = document.createElement("button");
            btnWatch.type = "button";
            btnWatch.className = "btn-watch";
            btnWatch.innerHTML = '<i class="fa-solid fa-play"></i> Học ngay';
            btnWatch.addEventListener("click", () => goToCourseVideo(course.id));

            // Ghép các nút vào button group
            buttonGroup.appendChild(btnDetail);
            buttonGroup.appendChild(btnWatch);

            // Ghép các phần tử vào card
            card.appendChild(titleEl);
            card.appendChild(descEl);
            card.appendChild(buttonGroup);

            myCoursesList.appendChild(card);
        });
    }

    // Fetch và render khóa học
    const courses = await fetchMyCourses(token);
    renderMyCourses(courses);

    // Menu handling
    if (userMenu) {
        userMenu.onclick = e => {
            e.stopPropagation();
            userMenu.classList.toggle("active");
        };
    }

    document.addEventListener("click", e => {
        if (userMenu && !userMenu.contains(e.target)) {
            userMenu.classList.remove("active");
        }
    });

    if (userMenu) {
        const userMenuUl = userMenu.querySelector("ul");
        if (userMenuUl) {
            userMenuUl.onclick = e => e.stopPropagation();
        }
    }

    // Logout
    if (logoutBtn) {
        logoutBtn.onclick = e => {
            e.preventDefault();
            userMenu.classList.remove("active");
            logout();
        };
    }

    // Menu links
    if (myCoursesLink) {
        myCoursesLink.onclick = e => {
            e.preventDefault();
            userMenu.classList.remove("active");
            window.location.href = "my-courses.html";
        };
    }

    if (profileLink) {
        profileLink.onclick = e => {
            e.preventDefault();
            userMenu.classList.remove("active");
            window.location.href = "profile.html";
        };
    }

    if (topupLink) {
        topupLink.onclick = e => {
            e.preventDefault();
            userMenu.classList.remove("active");
            window.location.href = "topup.html";
        };
    }

    if (contactLink) {
        contactLink.onclick = e => {
            e.preventDefault();
            userMenu.classList.remove("active");
            window.location.href = "contact.html";
        };
    }

    if (topupGuideLink) {
        topupGuideLink.onclick = e => {
            e.preventDefault();
            userMenu.classList.remove("active");
            window.location.href = "topup-guide.html";
        };
    }

    // Search functionality
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
});