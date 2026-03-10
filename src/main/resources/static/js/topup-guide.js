document.addEventListener("DOMContentLoaded", () => {
  // Lấy các phần tử DOM
  const userMenu = document.getElementById("user-menu");
  const authButtons = document.getElementById("auth-buttons");
  const menuName = document.getElementById("user-name");
  const menuAvatar = document.getElementById("user-avatar");
  const qrCodeCanvas = document.getElementById("qr-code");
  const btnTopupNow = document.getElementById("btn-topup-now");
  const notification = document.getElementById("notification");
  const logoutBtn = document.getElementById("logout-btn");
  const myCoursesLink = document.getElementById("my-courses-link");
  const profileLink = document.getElementById("profile-link");
  const topupLink = document.getElementById("topup-link");
  const contactLink = document.getElementById("contact-link");

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

  // Tạo mã QR
  if (qrCodeCanvas) {
    const paymentUrl = "https://payment.example.com/topup?userId=" + (user ? user.email : "guest");
    QRCode.toCanvas(qrCodeCanvas, paymentUrl, { width: 200 }, function (error) {
      if (error) {
        console.error("Lỗi tạo QR Code:", error);
        showNotification("Không thể tạo mã QR!", true);
      }
    });
  }

  // Xử lý nút Nạp tiền ngay
  if (btnTopupNow) {
    btnTopupNow.addEventListener("click", () => {
      if (!user) {
        showNotification("Vui lòng đăng nhập để nạp tiền!", true);
        setTimeout(() => {
          window.location.href = "login.html";
        }, 1500);
        return;
      }
      window.location.href = "topup.html";
    });
  }
});