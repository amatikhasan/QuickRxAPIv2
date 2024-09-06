<?php

require_once dirname(__FILE__) . '/MySqliStmt.php';

class AdminHandler
{

    private $con;

    public function __construct()
    {
        require_once dirname(__FILE__) . '/DbConnect.php';
        date_default_timezone_set('Asia/Dhaka');

        $db = new DbConnect();
        $this->con = $db->connect();

    }

    public function login($username, $password)
    {
        // Store the cipher method
        $ciphering = "AES-128-CTR";

        // Use OpenSSl Encryption method
        $iv_length = openssl_cipher_iv_length($ciphering);
        $options = 0;

        // Non-NULL Initialization Vector for encryption
        $encryption_iv = '1234567891011121';

        // Store the encryption key
        $encryption_key = "quickrx";

        // Use openssl_encrypt() function to encrypt the data
        $encryptedPassword = openssl_encrypt($password, $ciphering,
            $encryption_key, $options, $encryption_iv);

        $stmt = $this->con->prepare("SELECT id FROM admin WHERE username=? AND password=?");
        $stmt->bind_param("ss", $username, $encryptedPassword);
        $stmt->execute();
        $stmt->bind_result($id);
        if ($stmt->fetch()) {
            $stmt->close();
            return $id;
        } else {
            $stmt->close();
            return null;
        }
    }
    
    public function updatePassword($id, $password)
    {
        $now = new DateTime();
        $updated_at = $now->format("d M, Y h:i A");

        // Store the cipher method
        $ciphering = "AES-128-CTR";

        // Use OpenSSl Encryption method
        $iv_length = openssl_cipher_iv_length($ciphering);
        $options = 0;

        // Non-NULL Initialization Vector for encryption
        $encryption_iv = '1234567891011121';

        // Store the encryption key
        $encryption_key = "quickrx";

        // Use openssl_encrypt() function to encrypt the data
        $encryptedPassword = openssl_encrypt($password, $ciphering,
            $encryption_key, $options, $encryption_iv);

        $stmt = $this->con->prepare("UPDATE admin SET password=?, updated_at=? WHERE id=?");
        $stmt->bind_param("ssi", $encryptedPassword,$updated_at, $id);
        $stmt->execute();

        $affected_rows = $stmt->affected_rows;
        $stmt->close();
        if ($affected_rows > 0) {
            return "updated";
        }

        return "error";
    }

}

?>