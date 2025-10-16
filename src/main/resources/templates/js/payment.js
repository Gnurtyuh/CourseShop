
 document.addEventListener("DOMContentLoaded", () => {
      // Lấy các phần tử DOM
      const userMenu = document.getElementById("user-menu");
      const authButtons = document.getElementById("auth-buttons");
      const menuName = document.getElementById("user-name");
      const courseInfoDiv = document.getElementById("course-info");
      const qrCodeDiv = document.getElementById("qr-code");
      const paymentForm = document.getElementById("payment-form");
      const transactionCodeInput = document.getElementById("transaction-code");
      const btnPaid = document.getElementById("btn-paid");
      const btnCancel = document.getElementById("btn-cancel");
      const successMsg = document.getElementById("success-message");
      const notification = document.getElementById("notification");
      const logoutBtn = document.getElementById("logout-btn");
      const myCoursesLink = document.getElementById("my-course-link");
      const profileLink = document.getElementById("profile-link");
      const topupLink = document.getElementById("topup-link");
      const contactLink = document.getElementById("contact-link");
      const topupGuideLink = document.getElementById("topup-guide-link");

     const urlParams = new URLSearchParams(window.location.search);
     const amount = urlParams.get("amount");
     console.log("Số tiền cần thanh toán:", amount);

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
          window.location.href = "my-course.html";
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

         const form = document.getElementById("payment-form");
         const successMessage = document.getElementById("success-message");
         const countdownSpan = document.getElementById("countdown");

         const token = localStorage.getItem("userToken");

         form.addEventListener("submit", async function (e)  {
             e.preventDefault();
             const referenceCode = document.getElementById("transaction-code").value.trim();

             if (!referenceCode) {
                 alert("Vui lòng nhập mã giao dịch.");
                 return;
             }
             const status = "pending";
             try {
                 const response = await fetch("http://localhost:8080/CourseShop/api/users/payment/topup", {
                     method: "POST",
                     headers: {
                         "Content-Type": "application/json",
                         "Authorization": `Bearer ${token}`
                     },
                     body: JSON.stringify({ amount,status,referenceCode }) // chú ý key đúng với backend
                 });

                 if (response.ok) {
                     successMessage.style.display = "block";
                     let seconds = 5;
                     const countdownInterval = setInterval(() => {
                         seconds--;
                         countdownSpan.textContent = seconds;
                         if (seconds <= 0) {
                             clearInterval(countdownInterval);
                             alert("Đợi xét duyệt");
                             window.location.href = "index.html"; // điều hướng sau khi thành công
                         }
                     }, 10);
                 } else {
                     const error = await response.json();
                     alert("Lỗi khi xác nhận nạp tiền: " + (error.message || "Không xác định"));
                 }
             } catch (err) {
                 console.error("Gửi yêu cầu thất bại", err);
                 alert("Có lỗi xảy ra, vui lòng thử lại.");
             }

         btnCancel.addEventListener("click", () => {
             if (confirm("Bạn có chắc muốn hủy nạp tiền không?")) {
                 window.location.href = "index.html";
             }
         });
     });
    });