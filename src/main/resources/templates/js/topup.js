document.addEventListener("DOMContentLoaded", () => {
      // Lấy các phần tử DOM
      const userMenu = document.getElementById("user-menu");
      const authButtons = document.getElementById("auth-buttons");
      const menuName = document.getElementById("user-name");
      const notification = document.getElementById("notification");
      const logoutBtn = document.getElementById("logout-btn");
      const myCoursesLink = document.getElementById("my-courses-link");
      const profileLink = document.getElementById("profile-link");
      const topupLink = document.getElementById("topup-link");
      const contactLink = document.getElementById("contact-link");
      const topupGuideLink = document.getElementById("topup-guide-link");

      // Hàm hiển thị thông báo
      function showNotification(message, isError = false) {
        if (!notification) return console.error("Notification element not found");
        notification.textContent = message;
        notification.className = isError ? "error" : "";
        notification.style.display = "block";
        setTimeout(() => {
          notification.style.display = "none";
        }, 2500);
      }

      // Kiểm tra trạng thái đăng nhập
      async function checkLogin() {
          const token = localStorage.getItem("userToken");
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
              // Chuyển hướng đến trang đăng nhập
              showNotification("Vui lòng đăng nhập để thanh toán!", true);
              setTimeout(() => {
                  window.location.href = "login.html";
              }, 1500);
              return false;
          }
          return true;
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
      if (!checkLogin()) return;

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
    });

document.getElementById("topup-form").addEventListener("submit", function (e) {
    e.preventDefault();
    const amountInput = document.getElementById("amount");
    const amount = parseInt(amountInput.value);

    const message = document.getElementById("message");

    if (isNaN(amount) || amount < 10000) {
        message.textContent = "Số tiền nạp phải từ 10.000 VNĐ trở lên.";
        message.style.color = "red";
        return;
    }

    // ✅ Redirect sang payment.html kèm tham số
    window.location.href = `payment.html?amount=${amount}`;
});