<?php
require_once dirname(__FILE__) . '/Constants.php';
class PasswordHelper {
    public function encryptPassword($password) {
        $ciphering = "AES-128-CTR";
        $iv_length = openssl_cipher_iv_length($ciphering);
        $options = 0;

        // Non-NULL Initialization Vector for encryption
        //$encryption_iv = openssl_random_pseudo_bytes($iv_length); // Generate a random IV
        $encryption_iv = '1234567891011121';
        $encryption_key = "quickrx";

        $encryptedPassword = openssl_encrypt($password, $ciphering, $encryption_key, $options, $encryption_iv);

        return $encryptedPassword;
    }
    public function verifyPassword($password, $hash) {
        $result = password_verify($password, $hash);
        return $result;
    }

}
