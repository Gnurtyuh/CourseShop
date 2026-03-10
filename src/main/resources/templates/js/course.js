document.addEventListener("DOMContentLoaded", async () => {
  const userMenu = document.getElementById("user-menu");
  const authButtons = document.getElementById("auth-buttons");
  const menuName = document.getElementById("user-name");
  const courseTitle = document.getElementById("course-title");
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

  // Kiểm tra courseId có tồn tại không
  if (!courseId) {
    showNotification("Không tìm thấy mã khóa học!", true);
    setTimeout(() => window.location.href = "index.html", 2000);
    return;
  }

  // Biến lưu thông tin khóa học
  let courseInfo = null;
  let isPurchased = false;

  // Hàm hiển thị thông báo
  function showNotification(message, isError = false) {
    if (!notification) {
      console.error("Notification element not found");
      alert(message);
      return;
    }
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
      }
    } else {
      // Chưa đăng nhập: hiển thị auth-buttons, ẩn user-menu
      if (authButtons) authButtons.style.display = "flex";
      if (userMenu) userMenu.style.display = "none";
    }
    return user;
  }

  // Hàm kiểm tra khóa học đã mua chưa
  async function checkCoursePurchased() {
    if (!token) return false;

    try {
      const response = await fetch("http://localhost:8080/CourseShop/api/users/course/myCourse", {
        headers: { "Authorization": `Bearer ${token}` }
      });

      if (!response.ok) {
        console.error("Không thể lấy danh sách khóa học đã mua:", response.status);
        return false;
      }

      const myCourses = await response.json();
      return myCourses.some(course => course.id === parseInt(courseId));
    } catch (err) {
      console.error("Lỗi kiểm tra khóa học đã mua:", err);
      return false;
    }
  }

  // Hàm đăng xuất
  function logout() {
    localStorage.removeItem("user");
    localStorage.removeItem("userToken");
    showNotification("Bạn đã đăng xuất.");
    setTimeout(() => {
      window.location.href = "index.html";
    }, 1500);
  }

  // Hàm chuyển đến trang học
  function goToCourseVideo() {
    window.location.href = `course-video.html?courseId=${courseId}`;
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

    if (!data || data.length === 0) {
      sectionList.innerHTML = "<p>Khóa học này chưa có bài học nào.</p>";
      return;
    }

    sectionList.innerHTML = "";
    data.forEach(section => {
      const sectionDiv = document.createElement("div");
      sectionDiv.className = "course-section";

      const sectionTitle = document.createElement("h3");
      sectionTitle.textContent = section.title || "Không có tiêu đề";

      const lessonList = document.createElement("ul");

      if (section.lessons && section.lessons.length > 0) {
        section.lessons.forEach(lesson => {
          const li = document.createElement("li");
          li.textContent = lesson.title || "Bài học không có tiêu đề";
          lessonList.appendChild(li);
        });
      } else {
        const li = document.createElement("li");
        li.textContent = "Chưa có bài học nào trong phần này";
        li.style.fontStyle = "italic";
        li.style.color = "#6b7280";
        lessonList.appendChild(li);
      }

      sectionDiv.appendChild(sectionTitle);
      sectionDiv.appendChild(lessonList);
      sectionList.appendChild(sectionDiv);
    });
  }

  try {
    // Hiển thị trạng thái đang tải
    if (courseTitle) courseTitle.textContent = "Đang tải thông tin khóa học...";

    // Gọi API lấy chi tiết khóa học
    const resCourse = await fetch(`http://localhost:8080/CourseShop/api/public/courses/${courseId}`);

    if (!resCourse.ok) {
      throw new Error(`HTTP error! status: ${resCourse.status}`);
    }

    courseInfo = await resCourse.json();

    // Gọi API lấy danh sách bài học
    const resLessons = await fetch(`http://localhost:8080/CourseShop/api/public/courseSection/by-course/${courseId}`);

    if (!resLessons.ok) {
      console.warn("Không thể tải danh sách bài học:", resLessons.status);
    }

    const courseData = await resLessons.json();

    // Hiển thị chi tiết khóa học
    if (courseTitle) courseTitle.textContent = courseInfo.title || "Không có tiêu đề";
    if (courseDescription) {
      courseDescription.textContent = courseInfo.description || "Chưa có mô tả cho khóa học này.";
    }
    if (courseInstructor) courseInstructor.textContent = courseInfo.instructor || "ADMIN";
    if (coursePrice) {
      const price = courseInfo.price || 0;
      coursePrice.textContent = price.toLocaleString("vi-VN");
    }

    // Hiển thị danh sách bài học
    renderCourseSections(courseData);

    // Kiểm tra khóa học đã mua chưa (nếu đã đăng nhập)
    if (user && token) {
      isPurchased = await checkCoursePurchased();
    }

    // Xử lý hiển thị nút dựa trên trạng thái đã mua
    if (btnBuyCourse) {
      const buttonContainer = btnBuyCourse.parentNode;

      if (isPurchased) {
        // ĐÃ MUA: Ẩn nút mua và hiển thị nút xem khóa học
        btnBuyCourse.style.display = "none";

        // Tạo nút xem khóa học
        const btnWatchCourse = document.createElement("button");
        btnWatchCourse.id = "btn-watch-course";
        btnWatchCourse.className = "btn-watch-course";
        btnWatchCourse.innerHTML = '<i class="fa-solid fa-play"></i> Xem khóa học';
        btnWatchCourse.addEventListener("click", goToCourseVideo);

        // Thêm vào sau nút mua
        buttonContainer.appendChild(btnWatchCourse);

        // Thêm badge "Đã sở hữu" bên cạnh giá
        const priceElement = document.querySelector("#course-detail p strong + span")?.parentNode;
        if (priceElement) {
          const badge = document.createElement("span");
          badge.className = "owned-badge";
          badge.innerHTML = '<i class="fa-solid fa-check-circle"></i> Đã sở hữu';
          priceElement.appendChild(badge);
        }
      } else {
        // CHƯA MUA: Hiển thị nút mua và gắn sự kiện
        btnBuyCourse.style.display = "inline-block";

        // Xóa tất cả event listeners cũ bằng cách clone và thay thế
        const newBtnBuyCourse = btnBuyCourse.cloneNode(true);
        btnBuyCourse.parentNode.replaceChild(newBtnBuyCourse, btnBuyCourse);

        // Gắn event listener mới
        newBtnBuyCourse.addEventListener("click", async (e) => {
          e.preventDefault();

          if (!user || !token) {
            showNotification("Vui lòng đăng nhập để mua khóa học!", true);
            setTimeout(() => window.location.href = "login.html", 1500);
            return;
          }

          try {
            // Disable nút để tránh click nhiều lần
            newBtnBuyCourse.disabled = true;
            newBtnBuyCourse.textContent = "Đang xử lý...";
            newBtnBuyCourse.style.opacity = "0.7";

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
              showNotification("Mua khóa học thành công!");

              // Cập nhật lại user trong localStorage
              const updatedUser = { ...user };
              if (!updatedUser.myCourses) updatedUser.myCourses = [];
              updatedUser.myCourses.push({ id: courseInfo.id, title: courseInfo.title });
              localStorage.setItem("user", JSON.stringify(updatedUser));

              // Reload trang sau 1.5 giây để cập nhật giao diện
              setTimeout(() => window.location.reload(), 1500);
            } else if (result === "insufficient_balance") {
              showNotification("Số dư không đủ! Vui lòng nạp thêm tiền.", true);
              newBtnBuyCourse.disabled = false;
              newBtnBuyCourse.textContent = "Mua khóa học";
              newBtnBuyCourse.style.opacity = "1";
            } else {
              showNotification("Bạn đã mua khóa học này rồi!", true);
              newBtnBuyCourse.disabled = false;
              newBtnBuyCourse.textContent = "Mua khóa học";
              newBtnBuyCourse.style.opacity = "1";
            }
          } catch (err) {
            console.error("Lỗi khi mua khóa học:", err);
            showNotification("Lỗi kết nối server!", true);
            newBtnBuyCourse.disabled = false;
            newBtnBuyCourse.textContent = "Mua khóa học";
            newBtnBuyCourse.style.opacity = "1";
          }
        });
      }
    }

  } catch (err) {
    console.error("Lỗi khi load dữ liệu khóa học:", err);
    showNotification("Không thể tải thông tin khóa học!", true);

    // Hiển thị thông báo lỗi trong giao diện
    if (courseTitle) courseTitle.textContent = "Lỗi tải khóa học";
    if (courseDescription) courseDescription.textContent = "Đã xảy ra lỗi khi tải thông tin khóa học. Vui lòng thử lại sau.";
  }
});