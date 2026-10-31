package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.util.UserPrefs;

/**
 * 登录页（App 启动入口）。
 * 1. 手机号 + 密码 + 昵称，校验通过后用 SharedPreferences 保存登录状态（知识点：SharedPreferences）；
 * 2. 已登录或选择「先逛逛」的游客，启动时自动跳过本页，直接进入主页；
 * 3. 个人中心的「修改资料」也复用本页（edit_mode = true），此时按钮变成「保存」。
 */
public class LoginActivity extends BaseActivity {

    /** 编辑资料模式：从个人中心进入，只修改昵称与手机号，不跳转主页 */
    public static final String EXTRA_EDIT_MODE = "edit_mode";

    /** 强制显示登录页：游客主动点「登录」时使用，避免被自动跳过 */
    public static final String EXTRA_FORCE_SHOW = "force_show";

    private UserPrefs userPrefs;
    private boolean editMode;

    private EditText etPhone, etPassword, etNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userPrefs = new UserPrefs(this);
        editMode = getIntent().getBooleanExtra(EXTRA_EDIT_MODE, false);
        boolean forceShow = getIntent().getBooleanExtra(EXTRA_FORCE_SHOW, false);

        // 已经登录过（或选择过游客浏览）就直接进入主页，不用每次都看登录页
        if (!editMode && !forceShow && (userPrefs.isLogin() || userPrefs.isGuest())) {
            goMain();
            return;
        }

        etPhone = findViewById(R.id.et_login_phone);
        etPassword = findViewById(R.id.et_login_password);
        etNickname = findViewById(R.id.et_login_nickname);
        initViews();
    }

    private void initViews() {
        TextView tvTitle = findViewById(R.id.tv_login_title);
        TextView tvSubtitle = findViewById(R.id.tv_login_subtitle);
        TextView tvButton = findViewById(R.id.tv_login_button);
        View tvGuest = findViewById(R.id.tv_guest);

        if (editMode) {
            // 修改资料：回显已保存的信息，隐藏游客入口
            tvTitle.setText("修改资料");
            tvSubtitle.setText("修改后昵称与手机号会保存在本地");
            tvButton.setText("保存");
            tvGuest.setVisibility(View.GONE);
            findViewById(R.id.tv_agreement).setVisibility(View.GONE);
            if (userPrefs.isLogin()) {
                etNickname.setText(userPrefs.getNickname());
                etPhone.setText(userPrefs.getPhone());
            }
        } else {
            tvGuest.setOnClickListener(v -> {
                // 先逛逛：记录游客状态，下次启动不再拦截
                userPrefs.passAsGuest();
                Toast.makeText(this, "已进入游客模式，可随时在个人中心登录",
                        Toast.LENGTH_SHORT).show();
                goMain();
            });
        }

        tvButton.setOnClickListener(v -> onLoginClick());
    }

    /**
     * 登录 / 保存：对手机号与密码做基本校验
     */
    private void onLoginClick() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String nickname = etNickname.getText().toString().trim();

        if (phone.length() != 11 || !TextUtils.isDigitsOnly(phone) || !phone.startsWith("1")) {
            Toast.makeText(this, "请输入正确的 11 位手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        // 修改资料时只需要校验手机号，不用重新输入密码
        if (!editMode && password.length() < 6) {
            Toast.makeText(this, "密码至少需要 6 位", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(nickname)) {
            nickname = "水果会员";
        }

        // 保存登录状态与用户信息（SharedPreferences）
        userPrefs.login(nickname, phone);
        Toast.makeText(this, editMode ? "资料已保存" : "登录成功，欢迎你 " + nickname,
                Toast.LENGTH_SHORT).show();

        if (editMode) {
            setResult(RESULT_OK);
            finish();
        } else {
            goMain();
        }
    }

    /**
     * 进入主页，并清空登录页之前的历史，避免按返回键又回到登录页
     */
    private void goMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
