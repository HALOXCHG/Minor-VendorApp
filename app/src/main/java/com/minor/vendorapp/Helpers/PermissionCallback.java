package com.minor.vendorapp.Helpers;

import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.DexterError;

public interface PermissionCallback {

    void onPermissionsChecked(MultiplePermissionsReport report);

    void errorListener(DexterError error);
}
