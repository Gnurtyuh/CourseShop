document.addEventListener("DOMContentLoaded", () => {
  // Lấy các phần tử DOM
  const userMenu = document.getElementById("user-menu");
  const authButtons = document.getElementById("auth-buttons");
  const menuName = document.getElementById("user-name");
  const menuAvatar = document.getElementById("user-avatar");
  const courseTitle = document.getElementById("course-title");
  const courseDescription = document.getElementById("course-description");
  const courseVideo = document.getElementById("course-video");
  const lessonsList = document.getElementById("lessons");
  const notification = document.getElementById("notification");
  const logoutBtn = document.getElementById("logout-btn");
  const myCoursesLink = document.getElementById("my-courses-link");
  const profileLink = document.getElementById("profile-link");
  const topupLink = document.getElementById("topup-link");
  const contactLink = document.getElementById("contact-link");
  const topupGuideLink = document.getElementById("topup-guide-link");

  const token = localStorage.getItem("userToken");

  function showNotification(message, isError = false) {
    if (!notification) return console.error("Notification element not found");
    notification.textContent = message;
    notification.className = isError ? "error" : "";
    notification.style.display = "block";
    setTimeout(() => {
      notification.style.display = "none";
    }, 3000);
  }
  function redirectToLogin() {
    if (authButtons) authButtons.style.display = "flex";
    if (userMenu) userMenu.style.display = "none";
    showNotification("Vui lòng đăng nhập để xem thông tin cá nhân!", true);
    setTimeout(() => {
      window.location.href = "login.html";
    }, 1500);
  }
  // Kiểm tra trạng thái đăng nhập
  async function checkLogin() {

    if (!token) {
      redirectToLogin();
      return false;
    }
    const res = await fetch("http://localhost:8080/CourseShop/api/users/user/me", {
      headers: {
        "Authorization": "Bearer " + token,
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
      return {user, token};
    } else {
      if (authButtons) authButtons.style.display = "flex";
      if (userMenu) userMenu.style.display = "none";
      showNotification("Vui lòng đăng nhập để xem bài giảng!", true);
      setTimeout(() => {
        window.location.href = "login.html";
      }, 1500);
      return null;
    }
  }

  // Hàm đăng xuất
  function logout() {
    localStorage.removeItem("user");
    showNotification("Bạn đã đăng xuất.");
    setTimeout(() => {
      window.location.href = "index.html";
    }, 1500);
  }

  // Kiểm tra đăng nhập khi tải trang
  const user = checkLogin();
  if (!user) return;

  // Toggle menu người dùng
  if (userMenu) {
    userMenu.onclick = (e) => {
      e.stopPropagation();
      userMenu.classList.toggle("active");
    };
  }

  // Đóng menu khi click bên ngoài
  document.addEventListener("click", (e) => {
    if (userMenu && !userMenu.contains(e.target)) {
      userMenu.classList.remove("active");
    }
  });

  // Ngăn menu đóng khi click vào menu con
  if (userMenu) {
    const userMenuUl = userMenu.querySelector("ul");
    if (userMenuUl) {
      userMenuUl.onclick = (e) => {
        e.stopPropagation();
      };
    }
  }

  // Gắn sự kiện cho các liên kết trong menu
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

  // Lấy courseId từ URL
  const urlParams = new URLSearchParams(window.location.search);
  const courseId = urlParams.get("courseId");

  async function fetchCourse(url) {
    try {
      const response = await fetch(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.status === 401) {
        showNotification("Phiên đăng nhập đã hết hạn.", true);
        setTimeout(() => window.location.href = "login.html", 1500);
        return null;
      }

      if (!response.ok) {
        showNotification("Không tìm thấy khóa học!", true);
        setTimeout(() => window.location.href = "courses.html", 1500);
        return null;
      }

      return await response.json();
    } catch (error) {
      console.error("Fetch error:", error);
      showNotification("Đã xảy ra lỗi khi tải khóa học.", true);
      setTimeout(() => window.location.href = "my-courses.html", 1500);
      return null;
    }
  }

  // Kiểm tra quyền sở hữu khóa học
  async function loadCourseData(courseId) {
    try {
      // 1. Lấy thông tin khóa học
      const course = await fetchCourse(`http://localhost:8080/CourseShop/api/users/course/me/${courseId}`);
      courseTitle.textContent = course.name;
      courseDescription.textContent = course.description;

      // 2. Lấy danh sách sections
      const sections = await fetchCourse(`http://localhost:8080/CourseShop/api/users/courseSection/by-course/${courseId}`);

      if (!sections.length) {
        lessonsList.innerHTML = "<li>Khóa học chưa có chương nào.</li>";
        return;
      }

      lessonsList.innerHTML = "";

      let firstVideoSet = false;

      // 3. Lặp từng section để gọi tiếp /lessons
      for (const section of sections) {
        // Tạo tiêu đề section
        const sectionTitle = document.createElement("li");
        sectionTitle.innerHTML = `<strong>${section.title}</strong>`;
        sectionTitle.classList.add("section-title");
        lessonsList.appendChild(sectionTitle);

        // 4. Lấy lessons cho section đó
        const lessons = await fetchCourse(`http://localhost:8080/CourseShop/api/users/courseLesson/findAll?section_id=${section.id}`);

        lessons.forEach((lesson, index) => {
          const lessonItem = document.createElement("li");
          lessonItem.className = (!firstVideoSet && index === 0) ? "active" : "";

          lessonItem.innerHTML = `<a href="#" data-video="${lesson.videoUrl}">${lesson.title}</a>`;
          lessonsList.appendChild(lessonItem);

          // Auto phát video đầu tiên
          if (!firstVideoSet && index === 0) {
            courseVideo.src = lesson.videoUrl;
            courseVideo.load();
            courseVideo.style.display = "block";
            firstVideoSet = true;
          }

          // Click để phát video
          lessonItem.querySelector("a").addEventListener("click", (e) => {
            e.preventDefault();
            courseVideo.src = lesson.videoUrl;
            courseVideo.load();
            courseVideo.style.display = "block";
            lessonsList.querySelectorAll("li").forEach(li => li.classList.remove("active"));
            lessonItem.classList.add("active");
          });
        });
      }

    } catch (err) {
      console.error("Lỗi khi tải dữ liệu khóa học:", err);
      alert("Không thể tải dữ liệu khóa học. Vui lòng thử lại.");
      window.location.href = "courses.html";
    }
  }

// Gọi hàm khởi tạo
  loadCourseData(courseId);

  // Tìm khóa học theo ID và kiểm tra quyền sở hữu
  (async function () {
    if (!courseId) {
      showNotification("Không tìm thấy khóa học!", true);
      setTimeout(() => window.location.href = "my-courses.html", 1500);
      return;
    }



  })()
});