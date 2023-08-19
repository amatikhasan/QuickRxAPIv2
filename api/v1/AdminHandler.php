<?php

require_once dirname(__FILE__) . '/MySqliStmt.php';
require_once dirname(__FILE__) . '/Constants.php';
require_once dirname(__FILE__) . '/TokenHandler.php';
require_once dirname(__FILE__) . '/PasswordHelper.php';

class AdminHandler{
    private $con;

    public function __construct(){
        require_once dirname(__FILE__) . '/DbConnect.php';
        date_default_timezone_set('Asia/Dhaka');

        $db = new DbConnect();
        $this->con = $db->connect();
    }

    public function login($username, $password){
        $now = new DateTime();
        $updated_at = $now->format("d M, Y h:i A");

        $passwordHelper = new PasswordHelper();
        $encryptedPassword = $passwordHelper->encryptPassword($password);

        $stmt = $this->con->prepare("SELECT * FROM admin WHERE username=? AND password=? LIMIT 1");
        $stmt->bind_param("ss", $username, $encryptedPassword);

        $stmt->execute();
        $result = $stmt->get_result();

        $user = $result->fetch_assoc();
        if (!$user) {
            return 'error';
        }

        // Generate and sign the JWT token
        $tokenHandler= new TokenHandler();
        $jwtToken = $tokenHandler->createJWT($user['id'], $user['phone'], $user['email'], 'admin');

        if (!$jwtToken) {
            return 'error';
        }

        $stmt->close();

        $stmt = $this->con->prepare("UPDATE admin SET token=?,updated_at=? WHERE id=?");
        $stmt->bind_param("ssi", $jwtToken, $updated_at, $user['id']);
        $stmt->execute();

        $stmt->close();

        return $jwtToken;
    }

    public function updatePassword($password, $token){
        $now = new DateTime();
        $updated_at = $now->format("d M, Y h:i A");

        $passwordHelper = new PasswordHelper();
        $encryptedPassword = $passwordHelper->encryptPassword($password);

        $tokenHandler= new TokenHandler();
        $decodedToken = $tokenHandler->decodeToken($token);
        $id = $decodedToken->id;

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
