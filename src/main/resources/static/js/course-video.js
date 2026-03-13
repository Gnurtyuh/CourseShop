let player;

function onYouTubeIframeAPIReady() {
  player = new YT.Player("youtube-player", {
    height: "400",
    width: "100%",
    videoId: "",
    playerVars: {
      autoplay: 1,
      modestbranding: 1,
      rel: 0
    }
  });
}

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
      window.location.href = "login";
    }, 1500);
  }
  // Kiểm tra trạng thái đăng nhập
  async function checkLogin() {

    if (!token) {
      redirectToLogin();
      return false;
    }
    const res = await fetch("http://localhost:8080/api/users/user/me", {
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
        window.location.href = "login";
      }, 1500);
      return null;
    }
  }

  // Hàm đăng xuất
  function logout() {
    localStorage.removeItem("user");
    showNotification("Bạn đã đăng xuất.");
    setTimeout(() => {
      window.location.href = "index";
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
      window.location.href = "my-courses";
    };
  }

  if (profileLink) {
    profileLink.onclick = (e) => {
      e.preventDefault();
      userMenu.classList.remove("active");
      window.location.href = "profile";
    };
  }

  if (topupLink) {
    topupLink.onclick = (e) => {
      e.preventDefault();
      userMenu.classList.remove("active");
      window.location.href = "topup";
    };
  }

  if (contactLink) {
    contactLink.onclick = (e) => {
      e.preventDefault();
      userMenu.classList.remove("active");
      window.location.href = "contact";
    };
  }

  if (topupGuideLink) {
    topupGuideLink.onclick = (e) => {
      e.preventDefault();
      userMenu.classList.remove("active");
      window.location.href = "topup-guide";
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
        setTimeout(() => window.location.href = "login", 1500);
        return null;
      }

      if (!response.ok) {
        showNotification("Không tìm thấy khóa học!", true);
        setTimeout(() => window.location.href = "courses", 1500);
        return null;
      }

      return await response.json();
    } catch (error) {
      console.error("Fetch error:", error);
      showNotification("Đã xảy ra lỗi khi tải khóa học.", true);
      setTimeout(() => window.location.href = "my-courses", 1500);
      return null;
    }
  }
  function playVideo(url) {

    const video = document.getElementById("course-video");
    const youtube = document.getElementById("youtube-player");

    if (!url) return;

    // ===== YOUTUBE =====
    if (url.includes("youtube") || url.includes("youtu.be")) {

      video.pause();
      video.style.display = "none";

      youtube.style.display = "block";

      let videoId = "";

      if (url.includes("watch?v=")) {
        videoId = url.split("watch?v=")[1];
      } else {
        videoId = url.split("youtu.be/")[1];
      }

      if (player && player.loadVideoById) {
        player.loadVideoById(videoId);
      } else {
        youtube.src = `https://www.youtube.com/embed/${videoId}?autoplay=1`;
      }

    }

    // ===== VIDEO LOCAL =====
    else {

      youtube.style.display = "none";

      url = url.replace(/\\/g, "/");

      if (!url.startsWith("http")) {
        url = "http://localhost:8080" + url;
      }

      video.src = url;
      video.load();
      video.style.display = "block";
    }
  }
  // Kiểm tra quyền sở hữu khóa học
  async function loadCourseData(courseId) {
    try {

      const course = await fetchCourse(`http://localhost:8080/api/users/course/me/${courseId}`);
      courseTitle.textContent = course.name;
      courseDescription.textContent = course.description;

      const sections = await fetchCourse(`http://localhost:8080/api/users/courseSection/by-course/${courseId}`);

      if (!sections.length) {
        lessonsList.innerHTML = "<li>Khóa học chưa có chương nào.</li>";
        return;
      }

      lessonsList.innerHTML = "";

      let firstVideoSet = false;

      for (const section of sections) {

        const sectionTitle = document.createElement("li");
        sectionTitle.innerHTML = `<strong>${section.title}</strong>`;
        sectionTitle.classList.add("section-title");
        lessonsList.appendChild(sectionTitle);

        const lessons = await fetchCourse(`http://localhost:8080/api/users/courseLesson/findAll?section_id=${section.id}`);

        lessons.forEach((lesson, index) => {

          const lessonItem = document.createElement("li");

          lessonItem.innerHTML = `<a href="#">${lesson.title}</a>`;
          lessonsList.appendChild(lessonItem);

          // ===== AUTO VIDEO ĐẦU TIÊN =====
          if (!firstVideoSet && index === 0 && lesson.videoUrl) {
            playVideo(lesson.videoUrl);
            lessonItem.classList.add("active");
            firstVideoSet = true;
          }

          // ===== CLICK PHÁT VIDEO =====
          lessonItem.querySelector("a").addEventListener("click", (e) => {

            e.preventDefault();

            if (!lesson.videoUrl) {
              alert("Bài học chưa có video.");
              return;
            }

            playVideo(lesson.videoUrl);

            lessonsList.querySelectorAll("li").forEach(li => li.classList.remove("active"));
            lessonItem.classList.add("active");

          });

        });
      }

    } catch (err) {

      console.error("Lỗi khi tải dữ liệu khóa học:", err);
      alert("Không thể tải dữ liệu khóa học. Vui lòng thử lại.");
      window.location.href = "course";

    }
  }

// Gọi hàm khởi tạo
  loadCourseData(courseId);

  // Tìm khóa học theo ID và kiểm tra quyền sở hữu
  (async function () {
    if (!courseId) {
      showNotification("Không tìm thấy khóa học!", true);
      setTimeout(() => window.location.href = "my-courses", 1500);
      return;
    }



  })()
});