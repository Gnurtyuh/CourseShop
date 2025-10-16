document.getElementById('loginForm').addEventListener('submit', async function(e) {
  e.preventDefault();

  const name = document.getElementById('name').value;
  const password = document.getElementById('password').value;

  try {
    const response = await fetch('http://localhost:8080/CourseShop/api/public/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ name, password })
    });

    const result = await response.json();

    console.log(result.token)
      if (response.ok) {
        // Giả sử API trả về: { token, name, avatar, role }
        const user = {
          name: result.name,
          role: 'USER'
        };

        localStorage.setItem('user', JSON.stringify(user));
        localStorage.setItem('userToken', result.token);
      alert('Đăng nhập thành công!');
      window.location.href = 'index.html';
    } else {
      // Trường hợp đăng nhập không thành công
      alert(result.message || 'Sai email hoặc mật khẩu!');
    }
  } catch (error) {
    console.error('Lỗi khi gửi yêu cầu:', error);
    alert('Đã xảy ra lỗi. Vui lòng thử lại sau.');
  }
});
