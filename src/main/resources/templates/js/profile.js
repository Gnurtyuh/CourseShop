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
     const token = localStorage.getItem("userToken");
     async function checkLogin() {

         if (!token) {
             redirectToLogin();
             return false;
         }
         try {
             const res = await fetch("http://localhost:8080/CourseShop/api/users/user/me", {
                 headers: {
                     "Authorization": "Bearer " + token,
                 },
             });
             if (!res.ok) throw new Error("Token không hợp lệ");
             const user = await res.json();

             // Hiển thị giao diện người dùng
             if (authButtons) authButtons.style.display = "none";
             if (userMenu) {
                 userMenu.style.display = "flex";
                 if (menuName) menuName.textContent = user.name || "User" ;
             }

             if (userName) userName.textContent = user.name ;
             if (userEmail) userEmail.textContent = user.email ;
             if (userBalance) userBalance.textContent = Number(user.balance || 0).toLocaleString('vi-VN') + " VND";
             if (userCreated) userCreated.textContent = user.createdAt ;

             const myCourses = await fetch("http://localhost:8080/CourseShop/api/users/course/myCourse", {
                 headers: {
                     "Authorization": "Bearer " + token,
                 },
             });
             const myCourse = await myCourses.json();
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
                         li.textContent = course.title;
                         courseList.appendChild(li);
                     });
                 }
             }


         } catch (err) {
             console.error("Lỗi khi xác thực:", err.message);
             alert('Đã xảy ra lỗi. Vui lòng thử lại sau.' + err.message);
             redirectToLogin();
             return false;
         }
     }

     function redirectToLogin() {
         if (authButtons) authButtons.style.display = "flex";
         if (userMenu) userMenu.style.display = "none";
         showNotification("Vui lòng đăng nhập để xem thông tin cá nhân!", true);
         setTimeout(() => {
             window.location.href = "login.html";
         }, 1500);
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

      // Xử lý modal chỉnh sửa thông tin
      if (editProfileBtn) {
        editProfileBtn.onclick = () => {
          if (editProfileModal) editProfileModal.style.display = "flex";
          const user = JSON.parse(localStorage.getItem("user"));
          if (user) {
            document.getElementById("edit-name").value = user.name || "";
            document.getElementById("edit-email").value = user.email || "";
          }
        };
      }

      if (closeModal) {
        closeModal.onclick = () => {
          if (editProfileModal) editProfileModal.style.display = "none";
        };
      }

      // Xử lý modal đổi mật khẩu
      if (changePasswordBtn) {
        changePasswordBtn.onclick = () => {
          if (changePasswordModal) changePasswordModal.style.display = "flex";
        };
      }

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

      // Xử lý form chỉnh sửa thông tin
     if (editProfileForm) {
         editProfileForm.addEventListener("submit", async (e) => {
             e.preventDefault();
             const name = document.getElementById("edit-name").value.trim();
             const email = document.getElementById("edit-email").value.trim();

             try {
                 const response = await fetch(`http://localhost:8080/CourseShop/api/users/user`, {
                     method: "PUT",
                     headers: {
                         "Content-Type": "application/json",
                         "Authorization": `Bearer ${token}`,
                     },
                     body: JSON.stringify({name,email}),
                 });

                 const updatedUser = await response.json();
                 localStorage.setItem("user", JSON.stringify(updatedUser));
                 showNotification("Cập nhật thông tin thành công!");
                 if (editProfileModal) editProfileModal.style.display = "none";
                 await checkLogin();
             } catch (err) {
                 showNotification("Thành công về login", true);
             }
         });
     }


     if (changePasswordForm) {
         changePasswordForm.addEventListener("submit", async (e) => {
             e.preventDefault();

             const currentPassword = document.getElementById("current-password").value;
             const newPassword = document.getElementById("new-password").value;
             const confirmPassword = document.getElementById("confirm-password").value;
             const token = localStorage.getItem("token");

             if (newPassword !== confirmPassword) {
                 showNotification("Mật khẩu mới không khớp!", true);
                 return;
             }

             try {
                 const response = await fetch("http://localhost:8080/CourseShop/api/users/user", {
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

                 if (!response.ok) {
                     const error = await response.text();
                     showNotification(error || "Đổi mật khẩu thất bại!", true);
                     return;
                 }

                 showNotification("Đổi mật khẩu thành công!");
                 if (changePasswordModal) changePasswordModal.style.display = "none";
                 document.getElementById("current-password").value = "";
                 document.getElementById("new-password").value = "";
                 document.getElementById("confirm-password").value = "";
             } catch (err) {
                 showNotification("Lỗi kết nối đến máy chủ!", true);
             }
         });
     }

 });