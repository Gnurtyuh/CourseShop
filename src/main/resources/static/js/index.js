// Khởi tạo biến
const initialShow = 4;
const loadStep = 4;
let courses = [];
let myCourses = []; // Danh sách khóa học đã mua
let showCount = {};
let currentUser = null; // Thêm biến lưu thông tin user hiện tại

// Fetch dữ liệu khóa học
async function fetchCourses() {
  const token = localStorage.getItem("userToken");

  try {
    const response = await fetch("http://localhost:8080/api/public/courses", {
      headers: token ? { "Authorization": `Bearer ${token}` } : {}
    });

    if (!response.ok) throw new Error("Không thể tải danh sách khóa học!");

    courses = await response.json();

    // Nếu đã đăng nhập, lấy danh sách khóa học đã mua
    if (token) {
      await fetchMyCourses();
    }

    // Khởi tạo showCount theo category
    const categories = [...new Set(courses.map(c => c.category || 'Khác'))];
    categories.forEach(cat => {
      if (!showCount[cat]) showCount[cat] = initialShow;
    });

    renderCourses();

  } catch (err) {
    console.error("Lỗi fetchCourses:", err);
    showNotification("Lỗi tải khóa học!", true);
  }
}

// Fetch danh sách khóa học đã mua
async function fetchMyCourses() {
  const token = localStorage.getItem("userToken");

  try {
    const response = await fetch("http://localhost:8080/api/users/course/myCourse", {
      headers: { "Authorization": `Bearer ${token}` }
    });

    if (!response.ok) throw new Error("Không thể tải khóa học đã mua!");

    myCourses = await response.json();
    console.log("Khóa học đã mua:", myCourses);

  } catch (err) {
    console.error("Lỗi fetchMyCourses:", err);
    myCourses = [];
  }
}

// Fetch thông tin user
async function fetchUserInfo() {
  const token = localStorage.getItem("userToken");
  if (!token) return null;

  try {
    const response = await fetch("http://localhost:8080/api/users/user/me", {
      headers: { "Authorization": `Bearer ${token}` }
    });

    if (!response.ok) throw new Error("Không thể lấy thông tin user");

    const user = await response.json();
    currentUser = user;
    localStorage.setItem("user", JSON.stringify(user));
    return user;
  } catch (err) {
    console.error("Lỗi fetchUserInfo:", err);
    return null;
  }
}

// Kiểm tra khóa học đã mua chưa
function isCoursePurchased(courseId) {
  return myCourses.some(course => course.id === courseId);
}

// Render danh sách khóa học
function renderCourses(filteredCourses = null) {
  const container = document.getElementById("courses-container");
  if (!container) return;

  container.innerHTML = "";

  const data = filteredCourses || courses;
  if (!data || data.length === 0) {
    container.innerHTML = "<p style='text-align: center; padding: 20px;'>Không có khóa học nào.</p>";
    return;
  }

  const categories = [...new Set(data.map(c => c.category || 'Khoá học'))];
  const user = currentUser || JSON.parse(localStorage.getItem("user"));

  categories.forEach(category => {
    const categorySection = document.createElement("section");
    categorySection.classList.add("category-section");

    const h2 = document.createElement("h2");
    h2.textContent = category;
    categorySection.appendChild(h2);

    const courseList = document.createElement("div");
    courseList.classList.add("course-list");

    const coursesByCat = data.filter(c => (c.category || 'Khoá học') === category);
    let count = showCount[category] || initialShow;
    count = Math.min(count, coursesByCat.length);

    for (let i = 0; i < count; i++) {
      const course = coursesByCat[i];
      const isPurchased = isCoursePurchased(course.id);

      const courseItem = document.createElement("div");
      courseItem.classList.add("course-item");

      const title = document.createElement("h3");
      title.textContent = course.title || 'Không có tiêu đề';

      const instructor = document.createElement("p");
      instructor.innerHTML = `<strong>Giảng viên:</strong> ${course.instructor || 'Admin'}`;

      const desc = document.createElement("p");
      desc.textContent = course.description || 'Chưa có mô tả';

      const buttonGroup = document.createElement("div");
      buttonGroup.classList.add("button-group");

      // Nút chi tiết
      const btnDetail = document.createElement("button");
      btnDetail.textContent = "Chi tiết";
      btnDetail.classList.add("btn-detail");
      btnDetail.onclick = () => {
        const idEncoded = encodeURIComponent(course.id);
        window.location.href = `course.html?id=${idEncoded}`;
      };

      buttonGroup.appendChild(btnDetail);

      // Nút mua - chỉ hiển thị khi chưa mua
      if (!isPurchased) {
        const btnBuy = document.createElement("button");
        btnBuy.textContent = "Mua";
        btnBuy.classList.add("btn-buy");

        if (user) {
          btnBuy.onclick = async () => {
            const token = localStorage.getItem("userToken");

            try {
              const resPurchase = await fetch(
                  `http://localhost:8080/api/users/payment/buy-course?courseId=${course.id}`,
                  {
                    method: "POST",
                    headers: {
                      "Authorization": `Bearer ${token}`,
                      "Content-Type": "application/json"
                    }
                  }
              );

              const result = await resPurchase.text();

              if (result === "ok") {
                showNotification("Mua khóa học thành công!");
                // Cập nhật lại danh sách khóa học đã mua
                await fetchMyCourses();
                // Render lại danh sách
                renderCourses(filteredCourses);
              } else {
                showNotification("Số dư không đủ!", true);
              }
            } catch (err) {
              console.error("Lỗi khi mua khóa học:", err);
              showNotification("Lỗi kết nối server!", true);
            }
          };
        } else {
          btnBuy.onclick = () => {
            showNotification("Vui lòng đăng nhập để mua khóa học!", true);
            setTimeout(() => {
              window.location.href = "login.html";
            }, 1500);
          };
        }

        buttonGroup.appendChild(btnBuy);
      } else {
        // Thêm badge "Đã mua"
        const purchasedBadge = document.createElement("span");
        purchasedBadge.textContent = "Đã mua";
        purchasedBadge.style.backgroundColor = "#22c55e";
        purchasedBadge.style.color = "white";
        purchasedBadge.style.padding = "4px 8px";
        purchasedBadge.style.borderRadius = "4px";
        purchasedBadge.style.fontSize = "12px";
        purchasedBadge.style.marginLeft = "10px";
        title.appendChild(purchasedBadge);
      }

      courseItem.appendChild(title);
      courseItem.appendChild(instructor);
      courseItem.appendChild(desc);
      courseItem.appendChild(buttonGroup);

      courseList.appendChild(courseItem);
    }

    categorySection.appendChild(courseList);

    // Nút xem thêm
    if (count < coursesByCat.length) {
      const btnLoadMore = document.createElement("button");
      btnLoadMore.textContent = "Xem thêm";
      btnLoadMore.classList.add("btn-load-more");
      btnLoadMore.style.backgroundColor = "#007bff";
      btnLoadMore.style.color = "white";
      btnLoadMore.style.border = "none";
      btnLoadMore.style.padding = "10px 20px";
      btnLoadMore.style.borderRadius = "5px";
      btnLoadMore.style.cursor = "pointer";
      btnLoadMore.style.margin = "20px auto";
      btnLoadMore.style.display = "block";

      btnLoadMore.onclick = () => {
        showCount[category] = (showCount[category] || initialShow) + loadStep;
        renderCourses(filteredCourses);
      };

      categorySection.appendChild(btnLoadMore);
    }

    container.appendChild(categorySection);
  });
}

// Xử lý tìm kiếm
function handleSearch() {
  const query = document.getElementById("search-input").value.trim().toLowerCase();

  if (!query) {
    resetShowCount();
    renderCourses();
    return;
  }

  const filtered = courses.filter(c =>
      (c.title && c.title.toLowerCase().includes(query)) ||
      (c.instructor && c.instructor.toLowerCase().includes(query))
  );

  resetShowCount();
  renderCourses(filtered);
}

// Reset số lượng hiển thị
function resetShowCount() {
  for (let key in showCount) {
    showCount[key] = initialShow;
  }
}

// Hiển thị thông báo
function showNotification(message, isError = false) {
  const notification = document.getElementById("notification");
  if (!notification) return;

  notification.textContent = message;
  notification.className = isError ? "error" : "";
  notification.style.display = "block";

  setTimeout(() => {
    notification.style.display = "none";
  }, 2500);
}

// Kiểm tra và hiển thị thông tin đăng nhập
async function checkLogin() {
  const token = localStorage.getItem("userToken");
  const authArea = document.getElementById("auth-area");
  const userMenu = document.getElementById("user-menu");
  const userNameSpan = document.getElementById("user-name");

  if (token) {
    try {
      // Fetch thông tin user từ API
      const user = await fetchUserInfo();

      if (user && authArea && userMenu && userNameSpan) {
        authArea.style.display = "none";
        userMenu.style.display = "flex";
        userNameSpan.textContent = user.name || "User";
        return true;
      }
    } catch (err) {
      console.error("Lỗi checkLogin:", err);
    }
  }

  // Không có token hoặc lỗi
  if (authArea) authArea.style.display = "flex";
  if (userMenu) userMenu.style.display = "none";
  return false;
}

// Đăng xuất
function logout() {
  localStorage.removeItem("user");
  localStorage.removeItem("userToken");
  currentUser = null;
  myCourses = [];
  showNotification("Bạn đã đăng xuất.");
  checkLogin();
  renderCourses();
}

// Khởi tạo khi trang load
document.addEventListener('DOMContentLoaded', async () => {
  // Kiểm tra đăng nhập và hiển thị tên user
  await checkLogin();

  // Fetch dữ liệu khóa học
  await fetchCourses();

  // Xử lý user menu
  const userMenu = document.getElementById("user-menu");
  if (userMenu) {
    userMenu.onclick = (e) => {
      e.stopPropagation();
      userMenu.classList.toggle("active");
    };

    document.addEventListener("click", (e) => {
      if (userMenu && !userMenu.contains(e.target)) {
        userMenu.classList.remove("active");
      }
    });

    const menuList = userMenu.querySelector("ul");
    if (menuList) {
      menuList.onclick = (e) => {
        e.stopPropagation();
      };
    }
  }

  // Xử lý các link trong menu
  const logoutBtn = document.getElementById("logout-btn");
  if (logoutBtn) {
    logoutBtn.onclick = (e) => {
      e.preventDefault();
      if (userMenu) userMenu.classList.remove("active");
      logout();
    };
  }

  const myCoursesLink = document.getElementById("my-courses-link");
  if (myCoursesLink) {
    myCoursesLink.onclick = () => {
      if (userMenu) userMenu.classList.remove("active");
      window.location.href = "my-courses.html";
    };
  }

  const profileLink = document.getElementById("profile-link");
  if (profileLink) {
    profileLink.onclick = () => {
      if (userMenu) userMenu.classList.remove("active");
      window.location.href = "profile.html";
    };
  }

  const topupLink = document.getElementById("topup-link");
  if (topupLink) {
    topupLink.onclick = () => {
      if (userMenu) userMenu.classList.remove("active");
      window.location.href = "topup.html";
    };
  }

  const contactLink = document.getElementById("contact-link");
  if (contactLink) {
    contactLink.onclick = () => {
      if (userMenu) userMenu.classList.remove("active");
      window.location.href = "contact.html";
    };
  }

  const topupGuideLink = document.getElementById("topup-guide-link");
  if (topupGuideLink) {
    topupGuideLink.onclick = () => {
      if (userMenu) userMenu.classList.remove("active");
      window.location.href = "topup-guide.html";
    };
  }

  // Xử lý tìm kiếm
  const searchBtn = document.getElementById("search-btn");
  const searchInput = document.getElementById("search-input");

  if (searchBtn) {
    searchBtn.onclick = handleSearch;
  }

  if (searchInput) {
    searchInput.onkeydown = (e) => {
      if (e.key === "Enter") {
        handleSearch();
      }
    };
  }
});