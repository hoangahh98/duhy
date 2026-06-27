"""
Hệ thống xác thực cho Admin & VĐV
- Admin: email + password
- VĐV: email + password (123456789)
"""
from db import db_cursor
from functools import wraps
from flask import session, redirect, url_for
from config import normalize_admin_user

class AuthService:
    @staticmethod
    def hash_password(password):
        """Simple hash (trong production dùng werkzeug.security.generate_password_hash)"""
        import hashlib
        return hashlib.sha256(password.encode()).hexdigest()

    @staticmethod
    def verify_password(plain, hashed):
        """Verify password"""
        return AuthService.hash_password(plain) == hashed

    @staticmethod
    def login_admin(email, password):
        """Login cho admin"""
        login_name = normalize_admin_user(email)
        with db_cursor() as cursor:
            cursor.execute("""
                SELECT id, email, password, display_name
                FROM users
                WHERE role = 'admin'
                  AND (
                    lower(email) = lower(%s)
                    OR lower(split_part(email, '@', 1)) = lower(%s)
                  );
            """, (login_name, login_name))
            user = cursor.fetchone()

        if not user:
            return None, "Email không tồn tại"

        if not AuthService.verify_password(password, user[2]):
            return None, "Mật khẩu sai"

        email = normalize_admin_user(user[1])
        display_name = user[3] or ("Super Admin" if email == "admin" else email)
        return {"id": user[0], "email": email, "role": "admin", "display_name": display_name}, None

    @staticmethod
    def register_admin(email, password, display_name=None):
        """Tạo tài khoản admin (chỉ cấu hình lần đầu)"""
        admin_user = normalize_admin_user(email)
        display_name = (display_name or admin_user).strip()
        try:
            with db_cursor(commit=True) as cursor:
                cursor.execute("SELECT id FROM users WHERE role = 'admin' AND lower(email) = lower(%s);", (admin_user,))
                if cursor.fetchone():
                    return False, "User admin đã tồn tại"

                hashed = AuthService.hash_password(password)
                cursor.execute("""
                    INSERT INTO users (email, password, role, display_name) VALUES (%s, %s, %s, %s);
                """, (admin_user, hashed, "admin", display_name))
            return True, "Tạo admin thành công"
        except Exception as e:
            return False, str(e)


def login_required(f):
    """Decorator: chỉ cho phép user đã login"""
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if 'user' not in session:
            return redirect(url_for('login'))
        return f(*args, **kwargs)
    return decorated_function

def admin_required(f):
    """Decorator: chỉ cho phép admin"""
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if 'user' not in session or session['user'].get('role') != 'admin':
            return redirect(url_for('login'))
        return f(*args, **kwargs)
    return decorated_function

def vdv_required(f):
    """Decorator: chỉ cho phép VĐV"""
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if 'user' not in session or session['user'].get('role') != 'vdv':
            return redirect(url_for('login'))
        return f(*args, **kwargs)
    return decorated_function
