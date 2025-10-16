const initialShow = 4;
const loadStep = 4;
let courses = []; // Lưu danh sách từ API

const showCount = {}; // Sẽ khởi tạo động theo category

async function fetchCourses() {
  const token = localStorage.getItem("userToken");

  try {
    const response = await fetch("http://localhost:8080/CourseShop/api/public/courses", {
      headers: token ? { "Authorization": `Bearer ${token}` } : {}
    });

    if (!response.ok) throw new Error("Không thể tải danh sách khóa học!");

    courses = await response.json();

    // Khởi tạo showCount theo category
    const categories = [...new Set(courses.map(c => c.category))];
    categories.forEach(cat => {
      if (!showCount[cat]) showCount[cat] = initialShow;
    });

    renderCourses();

  } catch (err) {
    console.error("Lỗi fetchCourses:", err);
    showNotification("Lỗi tải khóa học!", true);
  }
}

function renderCourses(filteredCourses = null) {
  const container = document.getElementById("courses-container");
  container.innerHTML = "";

  const data = filteredCourses || courses;
  const categories = [...new Set(data.map(c => c.category))];

  categories.forEach(category => {
    const categorySection = document.createElement("section");
    categorySection.classList.add("category-section");

    const h2 = document.createElement("h2");
    h2.textContent = category;
    categorySection.appendChild(h2);

    const courseList = document.createElement("div");
    courseList.classList.add("course-list");

    const coursesByCat = data.filter(c => c.category === category);
    let count = showCount[category] || initialShow;
    count = Math.min(count, coursesByCat.length);

    for (let i = 0; i < count; i++) {
      const course = coursesByCat[i];
      const courseItem = document.createElement("div");
      courseItem.classList.add("course-item");

      const title = document.createElement("h3");
      title.textContent = course.title;

      const instructor = document.createElement("p");
      instructor.innerHTML = `<strong>Giảng viên:</strong> Admin`;

      const desc = document.createElement("p");
      desc.textContent = course.description;

      const buttonGroup = document.createElement("div");
      buttonGroup.classList.add("button-group");

      const btnDetail = document.createElement("button");
      btnDetail.textContent = "Chi tiết";
      btnDetail.classList.add("btn-detail");
      btnDetail.onclick = () => {
        const idEncoded = encodeURIComponent(course.id);
        window.location.href = `course.html?id=${idEncoded}`;
      };

      const btnBuy = document.createElement("button");
      btnBuy.textContent = "Mua";
      btnBuy.classList.add("btn-buy");

      btnBuy.onclick = async () => {
        const token = localStorage.getItem("userToken");

        if (!token) {
          showNotification("Vui lòng đăng nhập để mua khóa học!", true);
          setTimeout(() => {
            window.location.href = "login.html";
          }, 1500);
          return;
        }

        try {
          const response = await fetch(`http://localhost:8080/CourseShop/api/users/payment/buy-course?courseId=${course.id}`, {
            method: "POST",
            headers: {
              "Authorization": `Bearer ${token}`,
              "Content-Type": "application/json"
            },
          });

          if (response.ok) {
            alert("Mua khóa học thành công!");
            window.location.reload(); // Hoặc chuyển hướng tùy ý
          } else {
            const errorData = await response.json();
            showNotification(errorData.message || "Không thể mua khóa học!", true);
          }
        } catch (err) {
          console.error("Lỗi khi mua khóa học:", err);
          showNotification("Đã xảy ra lỗi!", true);
        }
      };


      buttonGroup.appendChild(btnDetail);
      buttonGroup.appendChild(btnBuy);

      courseItem.appendChild(title);
      courseItem.appendChild(instructor);
      courseItem.appendChild(desc);
      courseItem.appendChild(buttonGroup);

      courseList.appendChild(courseItem);
    }

    categorySection.appendChild(courseList);

    if (count < coursesByCat.length) {
      const btnLoadMore = document.createElement("button");
      btnLoadMore.textContent = "Xem thêm";
      btnLoadMore.classList.add("btn-buy");
      btnLoadMore.style.backgroundColor = "#007bff";
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

function handleSearch() {
  const query = document.getElementById("search-input").value.trim().toLowerCase();

  if (!query) {
    resetShowCount();
    renderCourses();
    return;
  }

  const filtered = courses.filter(c =>
      c.title.toLowerCase().includes(query) ||
      c.instructor.toLowerCase().includes(query)
  );

  resetShowCount();
  renderCourses(filtered);
}

function resetShowCount() {
  for (let key in showCount) {
    showCount[key] = initialShow;
  }
}

function showNotification(message, isError = false) {
  const notification = document.getElementById("notification");
  notification.textContent = message;
  notification.className = isError ? "error" : "";
  notification.style.display = "block";

  setTimeout(() => {
    notification.style.display = "none";
  }, 2500);
}

function checkLogin() {
  const user = JSON.parse(localStorage.getItem("user"));
  const authArea = document.getElementById("auth-area");
  const userMenu = document.getElementById("user-menu");
  const userNameSpan = document.getElementById("user-name");

  if (user) {
    authArea.style.display = "none";
    userMenu.style.display = "flex";
    userNameSpan.textContent = user.name || "User";
  } else {
    authArea.style.display = "flex";
    userMenu.style.display = "none";
  }
}

function logout() {
  localStorage.removeItem("user");
  showNotification("Bạn đã đăng xuất.");
  checkLogin();
}

window.onload = () => {
  checkLogin();
  renderCourses();

  const userMenu = document.getElementById("user-menu");

  userMenu.onclick = (e) => {
    e.stopPropagation();
    userMenu.classList.toggle("active");
  };

  document.addEventListener("click", (e) => {
    if (!userMenu.contains(e.target)) {
      userMenu.classList.remove("active");
    }
  });

  userMenu.querySelector("ul").onclick = (e) => {
    e.stopPropagation();
  };

  document.getElementById("logout-btn").onclick = () => {
    userMenu.classList.remove("active");
    logout();
  };
  document.getElementById("my-courses-link").onclick = () => {
    userMenu.classList.remove("active");
    window.location.href = "my-courses.html";
  };
  document.getElementById("profile-link").onclick = () => {
    userMenu.classList.remove("active");
    window.location.href = "profile.html";
  };
  document.getElementById("topup-link").onclick = () => {
    userMenu.classList.remove("active");
    window.location.href = "topup.html";
  };
  document.getElementById("contact-link").onclick = () => {
    userMenu.classList.remove("active");
    window.location.href = "contact.html";
  };
  document.getElementById("topup-guide-link").onclick = () => {
    userMenu.classList.remove("active");
    window.location.href = "topup-guide.html";
  };
  document.getElementById("search-btn").onclick = handleSearch;
  document.getElementById("search-input").onkeydown = (e) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };
};
function checkLoadMoreVisibility() {
  const coursesContainer = document.getElementById('courses-container');
  if (!coursesContainer) return;

  // Duyệt từng category section
  const sections = coursesContainer.querySelectorAll('.category-section');

  sections.forEach(section => {
    const courseList = section.querySelector('.course-list');
    const loadMoreBtn = section.querySelector('button.btn-buy'); // dùng class chung đã đặt

    if (!courseList || !loadMoreBtn) return;

    const courseItems = courseList.getElementsByClassName('course-item');
    if (courseItems.length === 0) {
      loadMoreBtn.style.display = 'none';
      return;
    }

    const containerWidth = courseList.offsetWidth;
    const itemWidth = courseItems[0].offsetWidth + parseFloat(getComputedStyle(courseList).gap || 0);
    const maxItemsPerRow = Math.floor(containerWidth / itemWidth);

    if (courseItems.length >= maxItemsPerRow) {
      loadMoreBtn.style.display = 'block';
    } else {
      loadMoreBtn.style.display = 'none';
    }
  });
}

document.addEventListener('DOMContentLoaded', () => {
  checkLogin();
  fetchCourses();

  document.getElementById("search-btn").onclick = handleSearch;
  document.getElementById("search-input").onkeydown = (e) => {
    if (e.key === "Enter") handleSearch();
  };

  // Gọi hàm kiểm tra khi tải trang và khi resize
  window.addEventListener('load', checkLoadMoreVisibility);
  window.addEventListener('resize', checkLoadMoreVisibility);

  // Giả định dữ liệu khóa học từ index.js (cần tích hợp với logic hiện tại)
  // Thêm sự kiện cho nút "Xem thêm" (cần điều chỉnh trong index.js)
  loadMoreBtn.addEventListener('click', () => {
    // Logic tải thêm khóa học (tùy thuộc vào index.js)
    console.log('Load more clicked');
  });
});