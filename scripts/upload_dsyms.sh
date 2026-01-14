#!/bin/bash
# Script to upload iOS dSYMs to Firebase Crashlytics
# This script should be added as a Run Script phase in Xcode

set -e

# Check if Firebase Crashlytics run script exists
if [ -f "${PODS_ROOT}/FirebaseCrashlytics/run" ]; then
    echo "Uploading dSYMs to Firebase Crashlytics (CocoaPods)..."
    "${PODS_ROOT}/FirebaseCrashlytics/run"
elif [ -f "${BUILD_DIR%/Build/*}/SourcePackages/checkouts/firebase-ios-sdk/Crashlytics/run" ]; then
    echo "Uploading dSYMs to Firebase Crashlytics (SPM)..."
    "${BUILD_DIR%/Build/*}/SourcePackages/checkouts/firebase-ios-sdk/Crashlytics/run"
else
    echo "Warning: Firebase Crashlytics run script not found!"
    echo "Make sure Firebase Crashlytics is properly installed via CocoaPods or SPM"
    exit 1
fi

echo "dSYM upload completed successfully"
