document.addEventListener("DOMContentLoaded", async () => {
  const userMenu = document.getElementById("user-menu");
  const authButtons = document.getElementById("auth-buttons");
  const menuName = document.getElementById("user-name");
  const menuAvatar = document.getElementById("user-avatar");
  const courseTitle = document.getElementById("course-title");
  // const courseImage = document.getElementById("course-image");
  const courseDescription = document.getElementById("course-description");
  const courseInstructor = document.getElementById("course-instructor");
  const coursePrice = document.getElementById("course-price");
  const btnBuyCourse = document.getElementById("btn-buy-course");
  const sectionList = document.getElementById("section-list");
  const notification = document.getElementById("notification");
  const logoutBtn = document.getElementById("logout-btn");
  const myCoursesLink = document.getElementById("my-courses-link");
  const profileLink = document.getElementById("profile-link");
  const topupLink = document.getElementById("topup-link");
  const contactLink = document.getElementById("contact-link");

  const urlParams = new URLSearchParams(window.location.search);
  const courseId = urlParams.get("id");
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

  // Kiểm tra trạng thái đăng nhập
  function checkLogin() {
    const user = JSON.parse(localStorage.getItem("user"));
    if (user) {
      // Đã đăng nhập: ẩn auth-buttons, hiển thị user-menu
      if (authButtons) authButtons.style.display = "none";
      if (userMenu) {
        userMenu.style.display = "flex";
        if (menuName) menuName.textContent = user.name || "User";
        if (menuAvatar) menuAvatar.src = user.avatar || "user-avatar.png";
      }
    } else {
      // Chưa đăng nhập: hiển thị auth-buttons, ẩn user-menu
      if (authButtons) authButtons.style.display = "flex";
      if (userMenu) userMenu.style.display = "none";
    }
    return user;
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
  function renderCourseSections(data) {
    if (!sectionList) return;
    sectionList.innerHTML = "";
    data.forEach(section => {
      const sectionDiv = document.createElement("div");
      sectionDiv.className = "course-section";

      const sectionTitle = document.createElement("h3");
      sectionTitle.textContent = section.title;

      const lessonList = document.createElement("ul");
      section.lessons.forEach(lesson => {
        const li = document.createElement("li");
        li.textContent = lesson.title;
        lessonList.appendChild(li);
      });

      sectionDiv.appendChild(sectionTitle);
      sectionDiv.appendChild(lessonList);
      sectionList.appendChild(sectionDiv);
    });
  }

  try {
    // Gọi API lấy chi tiết khóa học
    const resCourse = await fetch(`http://localhost:8080/CourseShop/api/public/courses/${courseId}`);
    const courseInfo = await resCourse.json();

    // Gọi API lấy danh sách bài học
    const resLessons = await fetch(`http://localhost:8080/CourseShop/api/public/courseSection/by-course/${courseId}`);
    const courseData = await resLessons.json();

    // Hàm hiển thị danh sách bài học


    // Hiển thị chi tiết khóa học
    if (courseTitle) courseTitle.textContent = courseInfo.title;
    // if (courseImage) courseImage.src = courseInfo.image;
    if (courseDescription) courseDescription.textContent = courseInfo.description;
    if (courseInstructor) courseInstructor.textContent = "ADMIN";
    if (coursePrice) coursePrice.textContent = courseInfo.price.toLocaleString("vi-VN");

    // Hiển thị danh sách bài học

    renderCourseSections(courseData);

    // Kiểm tra khóa học đã mua chưa
    if (user && user.myCourses && user.myCourses.some(course => course.id === courseInfo.id)) {
      if (btnBuyCourse) {
        btnBuyCourse.textContent = "Đã sở hữu";
        btnBuyCourse.disabled = true;
        btnBuyCourse.style.background = "#6b7280";
        btnBuyCourse.style.cursor = "not-allowed";
      }
    }
    if (btnBuyCourse) {
      btnBuyCourse.addEventListener("click", async () => {
        if (!user || !token) {
          showNotification("Vui lòng đăng nhập để mua khóa học!", true);
          setTimeout(() => window.location.href = "login.html", 1500);
          return;
        }

        // Gọi API mua khóa học
        const resPurchase = await fetch(`http://localhost:8080/CourseShop/api/users/payment/buy-course?courseId=${courseId}`, {
          method: "POST",
          headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
          },
        });
        const result = await resPurchase.text();
        if (result === "ok") {
          alert("Mua khóa học thành công!");
          window.location.reload();
        } else {
          alert("số dư không đủ");
        }
      });
    }
  } catch (err) {
    console.error("Lỗi khi load dữ liệu khóa học:", err);
    showNotification("Không thể tải thông tin khóa học!", true);
  }
});

