package com.yyh.configtest.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.Gravity;

import com.yyh.configtest.R;
import com.yyh.configtest.ui.base.BaseFragment;
import com.yyh.configtest.ui.activity.WelcomeActivity;
import com.yyh.configtest.utils.easypermissions.EasyPermissions;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;


/**
 * 6.0权限管理工具类
 */

public class PermissionUtil implements EasyPermissions.PermissionCallbacks {
    private static final String PACKAGE_URL_SCHEME = "package:";
    public static short REQUEST_CODE_PERMISSION_FRAGMENT = 123;
    public static final short REQUEST_CODE_PERMISSION_ACTIVITY = 124;
    public static final String DIVIDE = ",";

    private static PermissionUtil sInstance;
    private WeakReference<PermissonCallBack> mCalRef;
    private boolean mIsNeedFinsh;

    private PermissionUtil() {
    }

    public static PermissionUtil getInstance() {
        if (sInstance == null) {
            synchronized (PermissionUtil.class) {
                if (sInstance == null) {
                    sInstance = new PermissionUtil();
                }
            }
        }
        return sInstance;
    }

    public PermissionUtil setRequestCode(Short s) {
        REQUEST_CODE_PERMISSION_FRAGMENT = s;
        return this;
    }

    public PermissionUtil needToFinsh(boolean isNeedFinsh) {
        mIsNeedFinsh = isNeedFinsh;
        return this;
    }

    public static void showGoSettingDialog(final Context context, List<String> perms) {

        final IAlertDialog dialog = UIAction.newAlertDialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentGravity(Gravity.CENTER);
        dialog.setTitle(context.getString(R.string.permission_title));
        dialog.setMessage(String.format(context.getString(R.string.permission_hint), context.getString(R.string.app_name)) + getHints(context, perms));
        dialog.setButton(IAlertDialog.BUTTON_POSITIVE, context.getString(R.string.permission_setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse(PACKAGE_URL_SCHEME + context.getPackageName()));
//                if (context instanceof Welcome) {
//                    ((Welcome) context).startActivityForResult(intent, REQUEST_CODE_PERMISSION_ACTIVITY);
//                    return;
//                }
                if (context instanceof WelcomeActivity) {
                    ((WelcomeActivity) context).startActivityForResult(intent, REQUEST_CODE_PERMISSION_ACTIVITY);
                    return;
                }
                context.startActivity(intent);
            }
        });
        dialog.setButton(IAlertDialog.BUTTON_NEGATIVE, context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (context instanceof WelcomeActivity) {
                    ((WelcomeActivity) context).finish();
                }
//                else if (context instanceof MGBaseFragmentActivity) {
//                    ((MGBaseFragmentActivity) context).finish();
//                }
                else if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }
        });
        dialog.show();
    }

    public void showGoSettingDialogs(final Context context, List<String> perms) {

        final IAlertDialog dialog = UIAction.newAlertDialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentGravity(Gravity.CENTER);
        dialog.setTitle(context.getString(R.string.permission_title));
        dialog.setMessage(String.format(context.getString(R.string.permission_hint), context.getString(R.string.app_name)) + getHint(context, perms));
        dialog.setButton(IAlertDialog.BUTTON_POSITIVE, context.getString(R.string.permission_setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse(PACKAGE_URL_SCHEME + context.getPackageName()));
//                if (context instanceof Welcome) {
//                    ((Welcome) context).startActivityForResult(intent, REQUEST_CODE_PERMISSION_ACTIVITY);
//                    return;
//                }
                if (context instanceof WelcomeActivity) {
                    ((WelcomeActivity) context).startActivityForResult(intent, REQUEST_CODE_PERMISSION_ACTIVITY);
                    return;
                }
                context.startActivity(intent);
            }
        });
        dialog.setButton(IAlertDialog.BUTTON_NEGATIVE, context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mIsNeedFinsh) {
//                    if (context instanceof Welcome) {
//                        ((Welcome) context).finish();
//                    } else if (context instanceof MGBaseFragmentActivity) {
//                        ((MGBaseFragmentActivity) context).finish();
//                    }
                    if (context instanceof WelcomeActivity) {
                        ((WelcomeActivity) context).finish();
                    } else if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                }
            }
        });
        dialog.show();
    }


    private static String getHint(Context context, List<String> perms) {
        StringBuilder sb = new StringBuilder();
        String appName = context.getString(R.string.app_name);
        for (String perm : perms) {
            if (android.Manifest.permission.CAMERA.equals(perm)) {
                sb.append(context.getString(R.string.permission_read_contacts));
                sb.append(DIVIDE);
                sb.append(context.getString(R.string.permission_useage_camera));
            } else if (android.Manifest.permission.READ_CONTACTS.equals(perm)
                    || android.Manifest.permission.GET_ACCOUNTS.equals(perm)) {
                sb.append(context.getString(R.string.permission_read_contacts));
                sb.append(DIVIDE);
                sb.append(context.getString(R.string.permission_useage_contacts));
            } else if (android.Manifest.permission.RECORD_AUDIO.equals(perm)) {
                sb.append(context.getString(R.string.permission_record_audio));
                sb.append(DIVIDE);
                sb.append(context.getString(R.string.permission_useage_audio));
            } else if (android.Manifest.permission.ACCESS_FINE_LOCATION.equals(perm)) {
                sb.append(context.getString(R.string.permission_access_fine_location));
                sb.append(DIVIDE);
                sb.append(context.getString(R.string.permission_useage_location));

            } else if (android.Manifest.permission.READ_PHONE_STATE.equals(perm)) {
                sb.append(context.getString(R.string.permission_read_phone_state));
                sb.append(DIVIDE);
                sb.append(String.format(context.getString(R.string.permission_useage_state), appName));
            } else if (android.Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(perm)
                    || android.Manifest.permission.READ_EXTERNAL_STORAGE.equals(perm)) {
                sb.append(context.getString(R.string.permission_write_external_storage));
                sb.append(DIVIDE);
                sb.append(String.format(context.getString(R.string.permission_useage), appName));

            }
            return sb.toString();
        }
        return "";
    }

    public static String getHints(Context context, List<String> perms) {
        StringBuilder sb = new StringBuilder();
        String appName = context.getString(R.string.app_name);
        for (String perm : perms) {
            if (android.Manifest.permission.CAMERA.equals(perm)) {
                sb.append(context.getString(R.string.permission_camera));

            } else if (android.Manifest.permission.READ_CONTACTS.equals(perm)
                    || android.Manifest.permission.GET_ACCOUNTS.equals(perm)) {
                sb.append(context.getString(R.string.permission_read_contacts));
            } else if (android.Manifest.permission.RECORD_AUDIO.equals(perm)) {
                sb.append(context.getString(R.string.permission_record_audio));
            } else if (android.Manifest.permission.ACCESS_FINE_LOCATION.equals(perm)) {
                sb.append(context.getString(R.string.permission_access_fine_location));
            } else if (android.Manifest.permission.READ_PHONE_STATE.equals(perm)) {
                sb.append(context.getString(R.string.permission_read_phone_state));
            } else if (android.Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(perm)) {
                sb.append(context.getString(R.string.permission_write_external_storage));
            }
            sb.append(DIVIDE);
        }
        sb.append(String.format(context.getString(R.string.permission_useage), appName));
        return sb.toString();
    }


    /**
     * @param fragment
     * @param pers
     */
    public void request(BaseFragment fragment, String... pers) {
        if (fragment == null /*|| !fragment.isAdded()*/) {
            return;
        }
        PermissonCallBack callBack = PermissionUtil.getInstance().setCallBack(fragment);
        if (!EasyPermissions.hasPermissions(fragment.getActivity(), pers)) {
            EasyPermissions.requestPermissions(fragment, "", R.string.ok, R.string.cancel, REQUEST_CODE_PERMISSION_FRAGMENT, pers);
        } else {
            callBack.onPermissionsGranted(REQUEST_CODE_PERMISSION_FRAGMENT, Arrays.asList(pers));
        }
    }


    /**
     * @param activity
     * @param pers
     */
    public void request(Activity activity, String... pers) {
        PermissonCallBack callBack = PermissionUtil.getInstance().setCallBack(((PermissonCallBack) activity));
        if (!EasyPermissions.hasPermissions(activity, pers)) {
            EasyPermissions.requestPermissions(activity, "", R.string.ok, R.string.cancel, REQUEST_CODE_PERMISSION_ACTIVITY, pers);
        } else {
            callBack.onPermissionsGranted(REQUEST_CODE_PERMISSION_ACTIVITY, Arrays.asList(pers));
        }
    }

    public static boolean hasPer(Context context, String... pers) {
        return EasyPermissions.hasPermissions(context, pers);
    }

    public static void requestCallBack(int requestCode, String[] permissions, int[] grantResults, Object fragment) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, fragment);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (mCalRef != null) {
            mCalRef.get().onPermissionsGranted(requestCode, perms);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (mCalRef != null) {
            mCalRef.get().onPermissionsDenied(requestCode, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int i, @NonNull String[] strings, @NonNull int[] ints) {
        if (mCalRef != null) {
            mCalRef.get().onRequestPermissionsResult(i, strings, ints);
        }
    }

    public interface PermissonCallBack {

        void onPermissionsGranted(int requestCode, List<String> perms);

        void onPermissionsDenied(int requestCode, List<String> perms);

        void onRequestPermissionsResult(int i, @NonNull String[] strings, @NonNull int[] ints);
    }

    public PermissonCallBack setCallBack(PermissonCallBack callBack) {
        mCalRef = new WeakReference<PermissonCallBack>(callBack);
        return mCalRef.get();
    }
}
