// document.getElementById('loginAdminForm').addEventListener('submit', function(e) {
//   e.preventDefault();
//
//   const email = document.getElementById('email').value.trim();
//   const password = document.getElementById('password').value;
//   const loginBtn = document.getElementById('loginBtn');
//   const errorMessage = document.getElementById('errorMessage');
//
//   // Xóa thông báo lỗi cũ
//   errorMessage.style.display = 'none';
//   errorMessage.textContent = '';
//
//   // Vô hiệu hóa nút để tránh gửi nhiều lần
//   loginBtn.disabled = true;
//   loginBtn.textContent = 'Đang xử lý...';
//
//   // Danh sách admin hard-code
//   const admins = [
//     { id: 1, name: "Quản trị viên 1", email: "admin1@example.com", password: "admin123", role: "admin" },
//     { id: 2, name: "Quản trị viên 2", email: "admin2@example.com", password: "admin456", role: "admin" }
//   ];
//
//   // Kiểm tra email
//   const admin = admins.find(a => a.email === email);
//   if (!admin) {
//     errorMessage.textContent = 'Email quản trị viên không tồn tại!';
//     errorMessage.style.display = 'block';
//     loginBtn.disabled = false;
//     loginBtn.textContent = 'Đăng nhập';
//     return;
//   }
//
//   // Kiểm tra mật khẩu
//   if (admin.password === password) {
//     localStorage.setItem('admin', JSON.stringify({
//       id: admin.id,
//       name: admin.name,
//       email: admin.email,
//       role: 'admin'
//     }));
//     alert('Đăng nhập quản trị viên thành công!');
//     window.location.href = 'admin.html';
//   } else {
//     errorMessage.textContent = 'Mật khẩu không đúng!';
//     errorMessage.style.display = 'block';
//     loginBtn.disabled = false;
//     loginBtn.textContent = 'Đăng nhập';
//   }
// });
document.getElementById('loginAdminForm').addEventListener('submit', async function(e) {
  e.preventDefault();

  const name = document.getElementById('name').value;
  const password = document.getElementById('password').value;

  // Ẩn thông báo lỗi nếu có
  const errorMessage = document.getElementById('errorMessage');
  const loginBtn = document.getElementById('loginBtn');
  errorMessage.style.display = 'none';
  loginBtn.disabled = true;
  loginBtn.textContent = 'Đang đăng nhập...';

  try {
    const response = await fetch('http://localhost:8080/CourseShop/privateAd/AdAuthentication/AdminLogin', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({name, password})
    });

    const data = await response.json();

    if (response.ok && data.token) {
      // Lưu token vào localStorage
      localStorage.setItem('adminToken', data.token);

      // Tùy backend có thể trả thêm thông tin user trong payload hoặc kèm theo
      const admin = {
        name: data.sub || name,
        role: data.role || 'ADMIN'
      };
      localStorage.setItem('admin', JSON.stringify(admin));

      alert('Đăng nhập quản trị viên thành công!');
      window.location.href = 'admin.html';
    } else {
      throw new Error(data.message || 'Xác thực thất bại!');
    }

  } catch (err) {
    errorMessage.textContent = err.message || 'Có lỗi xảy ra!';
    errorMessage.style.display = 'block';
  } finally {
    loginBtn.disabled = false;
    loginBtn.textContent = 'Đăng nhập';
  }
});
