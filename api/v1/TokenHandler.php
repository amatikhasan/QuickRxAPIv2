<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

require_once dirname(__FILE__) . '/MySqliStmt.php';
require_once dirname(__FILE__) . '/Constants.php';
require 'vendor/autoload.php';
use \Firebase\JWT\JWT;
use Firebase\JWT\Key;

$key = JWT_SECRET_KEY; // This should be kept safe and not exposed!

class TokenHandler
{
    private $con;
    public function __construct()
    {
        require_once dirname(__FILE__) . '/DbConnect.php';
        date_default_timezone_set('Asia/Dhaka');

        $db = new DbConnect();
        $this->con = $db->connect();

    }

    function createJWT($userId, $phone, $email, $role) {
        global $key;

        $tokenPayload = array(
            "id" => $userId,
            "phone" => $phone,
            "email" => $email,
            "role" => $role,
            "iat" => time(),
            "exp" => time() + (7*24*60*60)  // Token expires in 7 days
        );

        $jwt = JWT::encode($tokenPayload, $key, 'HS256');
        return $jwt;
    }

    function validateJWT($token) {
        global $key;

        try {
            // Remove Bearer prefix if it exists
            $tokenString = str_replace('Bearer ', '', $token);
            $decodedToken = JWT::decode($tokenString, new Key($key, 'HS256'));
            $token_user_id = $decodedToken->id;
            $token_user_role = $decodedToken->id;
            // Prepare the SQL with placeholders to prevent SQL injection
            $stmt = null;
            if ($token_user_role == 'user') {
                $stmt = $this->con->prepare("SELECT id FROM users WHERE token=? LIMIT 1");
                $stmt->bind_param("s", $tokenString);
            } else if ($token_user_role == 'admin') {
                $stmt = $this->con->prepare("SELECT id FROM admin WHERE token=? LIMIT 1");
                $stmt->bind_param("s", $tokenString);
            }
            $stmt->execute();
            $stmt->bind_result($id);
            if ($stmt->fetch() && $id == $token_user_id) {
                $stmt->close();
                return true;
            }
            else{
                $stmt->close();
                return false;
            }
        } catch (Exception $e) {
            // Token is invalid or expired
            return false;
        }
    }

}
