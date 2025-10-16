
document.addEventListener("DOMContentLoaded", async () => {
  const sections = document.querySelectorAll(".content-section");
  const navItems = document.querySelectorAll(".sidebar-nav ul li[data-section]");
  const logoutBtn = document.getElementById("logout-btn");
  const createCourseForm = document.getElementById("create-course-form");

  const token = localStorage.getItem('adminToken');


  navItems.forEach((item) => {
    item.addEventListener("click", () => {
      navItems.forEach((i) => i.classList.remove("active"));
      item.classList.add("active");

      const sectionId = item.getAttribute("data-section");
      sections.forEach((section) => section.classList.remove("active"));
      document.getElementById(sectionId)?.classList.add("active");
    });
  });

  async function loadStatistics() {
    try {
      const res = await fetch("http://localhost:8080/CourseShop/api/admin/admin/statistics", {
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });

      if (!res.ok) throw new Error("Ko thể tải thống kê");

      const data = await res.json();

      document.getElementById("total-users").textContent = data.totalUsers.toLocaleString();
      document.getElementById("total-revenue").textContent = 2598000 +"VND";
      document.getElementById("top-course").textContent = "Đại số tuyến tính";


    } catch (error) {
      console.error(error);
      showNotification("Lỗi khi tải thống kê", true);
    }
  }

  // Biểu đồ thống kê
  function initChart() {
    const ctx = document.getElementById("statisticsChart").getContext("2d");
    new Chart(ctx, {
      type: "bar",
      data: {
        labels: ["Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5"],
        datasets: [
          {
            label: "Người dùng mới",
            data: [120, 150, 100, 200, 250],
            backgroundColor: "rgba(54, 162, 235, 0.6)",
          },
          {
            label: "Doanh thu (triệu VNĐ)",
            data: [10, 15, 7, 20, 22],
            backgroundColor: "rgba(255, 99, 132, 0.6)",
          },
        ],
      },
      options: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: "Thống kê hệ thống theo tháng",
            font: {size: 18},
          },
        },
      },
    });
  }

  // Đăng xuất
  logoutBtn?.addEventListener("click", () => {
    alert("Đăng xuất thành công!");
    window.location.href = "loginadmin.html";
  });

  createCourseForm?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const {title, description, price} = createCourseForm;

    if (!title.value.trim() || !description.value.trim() || !price.value.trim()) {
      return showNotification("Vui lòng điền đầy đủ thông tin!", true);
    }

    const newCourseData = {
      title: title.value.trim(),
      description: description.value.trim(),
      price: parseInt(price.value.trim())
    };
    console.log(newCourseData);
    console.log("Token:", token);
    try {
      const response = await fetch("http://localhost:8080/CourseShop/api/admin/courses", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(newCourseData)
      });
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Lỗi khi tạo khóa học");
      }
      const createdCourse = await response.json();
      showNotification(`Tạo khóa học thành công: ${createdCourse.title}`);
      createCourseForm.reset();

      await populateCourseDropdown();

      document.getElementById("course-select").value = createdCourse.id;

      await loadChapters(createdCourse.id);


      document.querySelector("#lessons-table tbody").innerHTML = "";
    } catch (error) {
      showNotification(`Lỗi: ${error.message}`, true);
    }
  });

  function switchTab(sectionId) {
    navItems.forEach((i) => i.classList.remove("active"));
    document.querySelector(`[data-section="${sectionId}"]`)?.classList.add("active");
    sections.forEach((s) => s.classList.remove("active"));
    document.getElementById(sectionId)?.classList.add("active");
  }

  async function loadCourses() {
    const tbody = document.querySelector("#courses-table tbody");
    tbody.innerHTML = "";
    try {
      const response = await fetch("http://localhost:8080/CourseShop/api/admin/courses", {
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
      if (!response.ok) throw new Error("Lỗi khi tải danh sách khóa học");
      const courses = await response.json();
      courses.forEach((course) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
        <td>${course.id}</td>
        <td>${course.title}</td>
        <td>${course.price.toLocaleString()} VNĐ</td>
        <td>
          <button class="btn-edit" data-id="${course.id}">Sửa</button>
          <button class="btn-delete" data-id="${course.id}">Xóa</button>
        </td>`;
        tbody.appendChild(tr);
      });
      document.querySelectorAll("#courses-table .btn-edit").forEach((btn) => {
        btn.addEventListener("click", () => editCourse(parseInt(btn.dataset.id)));
      });
      document.querySelectorAll("#courses-table .btn-delete").forEach((btn) => {
        btn.addEventListener("click", () => deleteCourse(parseInt(btn.dataset.id)));
      });
    } catch (error) {
      showNotification(error.message, true);
    }
  }
  async function fetchUsers() {
    try {
      const response = await fetch('http://localhost:8080/CourseShop/api/admin/admin/userAll', {
        headers: {
          "Authorization": `Bearer ${token}`
        }
      }); // hoặc URL đầy đủ
      if (!response.ok) {
        throw new Error('Lỗi khi lấy danh sách người dùng');
      }
      const user = await response.json();
      console.log('Danh sách người dùng:', user);
      return user;
    } catch (error) {
      console.error('Lỗi:', error);
      return [];
    }
  }
  async function editCourse(id) {
    try {
      const responseGet = await fetch(`http://localhost:8080/CourseShop/api/admin/courses/${id}`, {
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
      if (!responseGet.ok) throw new Error("Không tìm thấy khóa học");
      const course = await responseGet.json();
      const newTitle = prompt("Nhập tiêu đề mới:", course.title);
      if (newTitle === null) return;
      const newPriceStr = prompt("Nhập giá tiền mới (VNĐ):", course.price);
      if (newPriceStr === null) return;
      const newPrice = parseInt(newPriceStr);
      if (isNaN(newPrice) || newPrice < 0) return showNotification("Giá tiền không hợp lệ!", true);
      const responseUpdate = await fetch(`http://localhost:8080/CourseShop/api/admin/courses/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
          title: newTitle,
          price: newPrice
        })
      });
      if (!responseUpdate.ok) {
        const errData = await responseUpdate.json();
        throw new Error(errData.message || "Lỗi khi cập nhật khóa học");
      }
      showNotification("Cập nhật khóa học thành công!");
      await loadCourses();

    } catch (error) {
      showNotification(error.message, true);
    }
  }


  async function deleteCourse(id) {
    if (!confirm("Bạn có chắc muốn xóa khóa học?")) return;
    try {
      const response = await fetch(`http://localhost:8080/CourseShop/api/admin/courses/${id}`, {
        method: "DELETE",
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
      if (!response.ok) {
        const errData = await response.json();
        throw new Error(errData.message || "Lỗi khi xóa khóa học");
      }
      showNotification("Xóa khóa học thành công!");
      await loadCourses();
    } catch (error) {
      showNotification(error.message, true);
    }
  }

  async function loadUsers() {
    const users = await fetchUsers();  // Đợi dữ liệu trả về
    const tbody = document.querySelector("#users-table tbody");

    if (Array.isArray(users)) {
      users.forEach((user) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
        <td>${user.id}</td>
        <td>${user.name}</td>
        <td>${user.email}</td>
        <td>học viên</td>
        `;
        tbody.appendChild(tr);
      });
    } else {
      console.error("API không trả về mảng:", users);
    }
  }

  async function loadTopupRequests() {
    const tbody = document.querySelector("#topup-table tbody");
    tbody.innerHTML = "";

    try {
      // Gọi 1 API duy nhất (có chứa user lồng trong topup)
      const res = await fetch("http://localhost:8080/CourseShop/api/admin/payment/history", {
        headers: {"Authorization": `Bearer ${token}`}
      });

      if (!res.ok) throw new Error("Không thể tải danh sách yêu cầu nạp!");

      const users = await res.json(); // Mỗi phần tử là 1 user, chứa danh sách topups

      const tbody = document.querySelector("#topup-table tbody");
      tbody.innerHTML = "";

      users.forEach((user) => {
        user.topups.forEach((req) => {
          const statusText = req.status === "pending" ? "Chờ duyệt" :
              req.status === "success" ? "Thành công" : "Thất bại";

          const tr = document.createElement("tr");
          tr.setAttribute("data-id", req.id);
          tr.innerHTML = `
      <td>${req.id}</td>
      <td>${req.referenceCode}</td>
      <td>${user.id}</td>
      <td>${user.name}</td>
      <td>${req.amount.toLocaleString()} VNĐ</td>
      <td class="status">${statusText}</td>
      <td>${new Date(req.createAt).toLocaleString("vi-VN")}</td>
      <td class="actions">
        ${req.status === "pending" ? `
          <button class="btn-approve" data-id="${req.id}">Duyệt</button>
          <button class="btn-reject" data-id="${req.id}">Từ chối</button>
        ` : ""}
      </td>
    `;
          tbody.appendChild(tr);
        });
      });


      // Gắn sự kiện cho các nút
      document.querySelectorAll(".btn-approve").forEach((btn) =>
          btn.addEventListener("click", () =>
              updateTopupStatus(parseInt(btn.dataset.id), "success"))
      );

      document.querySelectorAll(".btn-reject").forEach((btn) =>
          btn.addEventListener("click", () =>
              updateTopupStatus(parseInt(btn.dataset.id), "failed"))
      );

    } catch (err) {
      showNotification(err.message, true);
    }
  }
  async function updateTopupStatus(id, status) {
    try {
      const res = await fetch(`http://localhost:8080/CourseShop/api/admin/payment/${id}/status`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({status})
      });
      const result = await res.text();
      if (result === "no") throw new Error("Cập nhật trạng thái thất bại!");

      showNotification(`Yêu cầu ${id} đã được ${status === "success" ? "duyệt" : "từ chối"}!`);
      await loadTopupRequests(); // Tải lại danh sách sau khi cập nhật

    } catch (err) {
      showNotification(err.message, true);
    }
  }
  async function populateCourseDropdown() {
    const courseSelect = document.getElementById("course-select");
    courseSelect.innerHTML = '<option value="">Chọn khóa học</option>';

    try {
      const response = await fetch("http://localhost:8080/CourseShop/api/admin/courses", {
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });

      if (!response.ok) throw new Error("Không thể tải danh sách khóa học");

      const data = await response.json();

      data.forEach(course => {
        const option = document.createElement("option");
        option.value = course.id;
        option.textContent = course.title;
        courseSelect.appendChild(option);
      });

    } catch (error) {
      console.error("Lỗi khi tải danh sách khóa học:", error);
      showNotification("Lỗi khi tải danh sách khóa học!", true);
    }
  }
  async function populateChapterDropdown(courseId) {
    const chapterSelect = document.getElementById("chapter-select");
    chapterSelect.innerHTML = '<option value="">Chọn chương</option>';

    try {
      const response = await fetch(`http://localhost:8080/CourseShop/api/admin/courseSection/by-course/${courseId}`, {
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });

      if (!response.ok) throw new Error("Không thể tải danh sách chương");

      const chapters = await response.json();

      chapters.forEach(chapter => {
        const option = document.createElement("option");
        option.value = chapter.id;
        option.textContent = chapter.title;
        chapterSelect.appendChild(option);
      });

    } catch (error) {
      console.error("Lỗi khi tải chương học:", error);
      showNotification("Lỗi khi tải chương học!", true);
    }
  }


  async function loadChapters(courseId) {
    try {
      const res = await fetch(`http://localhost:8080/CourseShop/api/admin/courseSection/by-course/${courseId}`, {
        headers: {"Authorization": `Bearer ${token}`}
      });
      if (!res.ok) throw new Error("Lỗi khi tải chương");
      const chapters = await res.json();

      const tbody = document.querySelector("#chapters-table tbody");
      tbody.innerHTML = "";

      chapters.forEach(chapter => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
        <td>${chapter.id}</td>
        <td>${chapter.title}</td>
        <td>${chapter.sectionOrder}</td>
        <td>
          <button class="btn-edit" data-course-id="${courseId}" data-chapter-id="${chapter.id}">Sửa</button>
          <button class="btn-delete" data-course-id="${courseId}" data-chapter-id="${chapter.id}">Xóa</button>
        </td>`;
        tbody.appendChild(tr);
      });

      document.querySelectorAll("#chapters-table .btn-edit").forEach(btn => {
        btn.addEventListener("click", () => editChapter(courseId, parseInt(btn.dataset.chapterId)));
      });
      document.querySelectorAll("#chapters-table .btn-delete").forEach(btn => {
        btn.addEventListener("click", () => deleteChapter(courseId, parseInt(btn.dataset.chapterId)));
      });

    } catch (err) {
      showNotification(err.message, true);
    }
  }

  async function loadLessons(courseId, sectionId) {
    try {
      const res = await fetch(`http://localhost:8080/CourseShop/api/admin/courseLesson/${sectionId}`, {
        headers: {"Authorization": `Bearer ${token}`}
      });
      if (!res.ok) throw new Error("Lỗi khi tải bài học");
      const lessons = await res.json();

      const tbody = document.querySelector("#lessons-table tbody");
      tbody.innerHTML = "";

      lessons.forEach(lesson => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
        <td>${lesson.id}</td>
        <td>${lesson.title}</td>
        <td>${lesson.content || "N/A"}</td>
        <td>${lesson.videoUrl || "N/A"}</td>
        <td>${lesson.lessonOrder}</td>
        <td>
          <button class="btn-edit" data-course-id="${courseId}" data-chapter-id="${sectionId}" data-lesson-id="${lesson.id}">Sửa</button>
          <button class="btn-delete" data-course-id="${courseId}" data-chapter-id="${sectionId}" data-lesson-id="${lesson.id}">Xóa</button>
        </td>`;
        tbody.appendChild(tr);
      });

      document.querySelectorAll("#lessons-table .btn-edit").forEach(btn => {
        btn.addEventListener("click", () => editLesson(courseId, sectionId, parseInt(btn.dataset.lessonId)));
      });
      document.querySelectorAll("#lessons-table .btn-delete").forEach(btn => {
        btn.addEventListener("click", () => deleteLesson(courseId, sectionId, parseInt(btn.dataset.lessonId)));
      });

    } catch (err) {
      showNotification(err.message, true);
    }
  }


  document.getElementById("create-chapter-form")?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const courseId = parseInt(document.getElementById("course-select").value);
    const title = document.getElementById("chapter-title").value.trim();
    const order = parseInt(document.getElementById("chapter-order").value);

    if (!courseId || !title || isNaN(order)) return showNotification("Vui lòng điền đầy đủ thông tin!", true);

    try {
      const res = await fetch(`http://localhost:8080/CourseShop/api/admin/courseSection/by-course/${courseId}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
          title,
          sectionOrder: order
        })
      });
      if (!res.ok) throw new Error("Tạo chương thất bại");
      showNotification(`Tạo chương thành công: ${title}`);
      e.target.reset();
      await loadChapters(courseId);
      await populateChapterDropdown(courseId);
    } catch (err) {
      showNotification(err.message, true);
    }
  });

  async function createLesson(courseId, chapterId, lessonData) {
    try {
      const res = await fetch(`http://localhost:8080/CourseShop/api/admin/courseLesson/by-course/${chapterId}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(lessonData)
      });

      if (!res.ok) throw new Error("Tạo bài học thất bại");
      return true;
    } catch (err) {
      console.error("Lỗi khi tạo bài học:", err);
      showNotification(err.message, true);
      return false;
    }
  }

  document.getElementById("create-lesson-form")?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const courseId = parseInt(document.getElementById("course-select").value);
    const chapterId = parseInt(document.getElementById("chapter-select").value);
    const title = document.getElementById("lesson-title").value.trim();
    const content = document.getElementById("lesson-content").value.trim() ;
    const videoUrl = document.getElementById("lesson-video").value.trim() || null;
    const order = parseInt(document.getElementById("lesson-order").value);

    if (!courseId || !chapterId || !title || isNaN(order)) {
      return showNotification("Vui lòng điền đầy đủ thông tin!", true);
    }

    const lessonData = {
      title,
      content,
      videoUrl: videoUrl,
      lessonOrder: order
    };

    const success = await createLesson(courseId, chapterId, lessonData);
    if (success) {
      showNotification(`Tạo bài học thành công: ${title}`);
      e.target.reset();
      await loadLessons(courseId, chapterId);
    }
  });


  async function editChapter(courseId, id) {
    try {
      // Lấy chapter hiện tại từ API (hoặc cache nếu có)
      const res = await fetch(`http://localhost:8080/CourseShop/api/admin/courseSection/${id}`, {
        headers: {"Authorization": `Bearer ${token}`}
      });
      if (!res.ok) throw new Error("Không tìm thấy chương");
      const chapter = await res.json();

      const newTitle = prompt("Nhập tiêu đề mới:", chapter.title);
      if (newTitle === null) return;

      const newOrder = prompt("Nhập thứ tự mới:", chapter.section_order);
      if (newOrder === null) return;
      const orderNum = parseInt(newOrder);
      if (isNaN(orderNum)) return showNotification("Thứ tự không hợp lệ!", true);

      const updateRes = await fetch(`http://localhost:8080/CourseShop/api/admin/courseSection/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
          title: newTitle.trim(),
          sectionOrder: orderNum
        })
      });
      if (!updateRes.ok) throw new Error("Cập nhật chương thất bại");
      showNotification("Cập nhật chương thành công!");
      await loadChapters(courseId);
      await populateChapterDropdown(courseId);
    } catch (err) {
      showNotification(err.message, true);
    }
  }


  async function deleteChapter(courseId, id) {
    if (!confirm("Bạn có chắc muốn xóa chương này? Tất cả bài học trong chương cũng sẽ bị xóa!")) return;

    try {
      const res = await fetch(`http://localhost:8080/CourseShop/api/admin/courseSection/${id}`, {
        method: "DELETE",
        headers: {"Authorization": `Bearer ${token}`}
      });
      if (!res.ok) throw new Error("Xóa chương thất bại");
      showNotification("Xóa chương thành công!");
      await loadChapters(courseId);
      await populateChapterDropdown(courseId);
    } catch (err) {
      showNotification(err.message, true);
    }
  }


  async function editLesson(courseId, chapterId, lessonId) {
    try {
      const res = await fetch(`http://localhost:8080/CourseShop/api/admin/courseLesson/lesson/${lessonId}`, {
        headers: {"Authorization": `Bearer ${token}`}
      });
      if (!res.ok) throw new Error("Không tìm thấy bài học");
      const lesson = await res.json();

      const newTitle = prompt("Nhập tiêu đề mới:", lesson.title);
      if (newTitle === null) return;

      const newContent = prompt("Nhập nội dung mới:", lesson.content );
      const newVideoUrl = prompt("Nhập URL video mới (để trống nếu không có):", lesson.videoUrl || "");
      const newOrder = prompt("Nhập thứ tự mới:", lesson.lessonOrder);
      if (newOrder === null) return;

      const orderNum = parseInt(newOrder);
      if (isNaN(orderNum)) return showNotification("Thứ tự không hợp lệ!", true);

      const updateRes = await fetch(`http://localhost:8080/CourseShop/api/admin/courseLesson/lesson/${lessonId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
          title: newTitle,
          content: newContent ,
          videoUrl: newVideoUrl,
          lessonOrder: orderNum
        })
      });
      if (!updateRes.ok) throw new Error("Cập nhật bài học thất bại");
      showNotification("Cập nhật bài học thành công!");
      await loadLessons(courseId, chapterId);
    } catch (err) {
      showNotification(err.message, true);
    }
  }


  async function deleteLesson(courseId, chapterId, lessonId) {
    if (!confirm("Bạn có chắc muốn xóa bài học này?")) return;

    try {
      const res = await fetch(`http://localhost:8080/CourseShop/api/admin/courseLesson/lesson/${lessonId}`, {
        method: "DELETE",
        headers: {"Authorization": `Bearer ${token}`}
      });
      if (!res.ok) throw new Error("Xóa bài học thất bại");
      showNotification("Xóa bài học thành công!");
      await loadLessons(courseId, chapterId);
    } catch (err) {
      showNotification(err.message, true);
    }
  }
  document.getElementById("course-select")?.addEventListener("change", (e) => {
    const courseId = e.target.value;
    if (courseId) {
      loadChapters(courseId);
      populateChapterDropdown(courseId);
      document.querySelector("#lessons-table tbody").innerHTML = "";
    }
  });
  document.getElementById("chapter-select")?.addEventListener("change", (e) => {
    const courseId = document.getElementById("course-select").value;
    const chapterId = e.target.value;
    if (courseId && chapterId) {
      loadLessons(courseId, chapterId);
    }
  });


  document.getElementById("folder-select")?.addEventListener("click", () => {
    document.getElementById("folder-input").click();
  });

  document.getElementById("folder-input")?.addEventListener("change", async (event) => {
    const files = event.target.files;
    const courseId = parseInt(document.getElementById("course-select").value);
    const chapterId = parseInt(document.getElementById("chapter-select").value);

    if (!chapterId) {
      return showNotification("Vui lòng chọn chương!", true);
    }

    if (files.length > 0) {
      try {
        const sortedFiles = Array.from(files).sort((a, b) =>
            a.name.localeCompare(b.name, undefined, { numeric: true })
        );

        for (let i = 0; i < sortedFiles.length; i++) {
          const file = sortedFiles[i];
          const fileName = file.name;
          const baseName = fileName.substring(0, fileName.lastIndexOf(".")) || fileName;
          const videoUrl = `/videos/${file.name}`;

          const lessonData = {
            title: baseName,
            content: baseName,
            video_url: videoUrl, // Có thể sửa lại đường dẫn nếu backend cần
            lesson_order: i + 1
          };

          await createLesson(courseId, chapterId, lessonData);
        }

        showNotification("Tự động thêm bài học thành công!");
        await loadLessons(courseId, chapterId);
      } catch (err) {
        console.error(err);
        showNotification("Có lỗi khi thêm bài học!", true);
      }
    }
  });
  function showNotification(message, isError = false) {
    const notification = document.getElementById("notification");
    notification.textContent = message;
    notification.style.display = "block";
    notification.className = isError ? "error" : "";
    setTimeout(() => notification.style.display = "none", 3000);
  }
  await populateCourseDropdown()
  await loadStatistics();
  await initChart();
  await loadCourses();
  await loadUsers();
  await loadTopupRequests();

});




