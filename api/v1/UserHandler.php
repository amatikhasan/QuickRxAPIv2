<?php

require_once dirname(__FILE__) . '/MySqliStmt.php';

class UserHandler
{
 
    
    private $con;
   
 
    public function __construct()
    {
        require_once dirname(__FILE__) . '/DbConnect.php';
        date_default_timezone_set('Asia/Dhaka');
 
        $db = new DbConnect();
        $this->con = $db->connect();
        
    }

    public function isUserAvailable($email,$phone)
    {

        $stmt = $this->con->prepare("SELECT id FROM users WHERE email='$email' OR phone='$phone'");
        $stmt->execute();
        $stmt->bind_result($id);
        if ($stmt->fetch()) {
            $stmt->close();
            return true;
        }
        else{
            $stmt->close();
            return false;
        }
    }
    
    public function isAccountRegistered($phone)
    {
        $stmt = $this->con->prepare("SELECT id FROM users WHERE phone LIKE '%$phone'");
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
    

    public function login($emailOrPhone, $password)
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

        $stmt = $this->con->prepare("SELECT * FROM users WHERE (email='$emailOrPhone' OR phone='$emailOrPhone' OR phone LIKE '%$emailOrPhone') AND password='$encryptedPassword'");
        $stmt->execute();
        $stmt->bind_result($id);
        if ($stmt->fetch()) {
            $stmt->close();
            return true;
        }
        else{
            $stmt->close();
            return false;
        }
    }
    
    public function loginReturnUserDetails($emailOrPhone, $password)
    {

        $user = array();
        
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

        $stmt = $this->con->prepare("SELECT * FROM users WHERE (email='$emailOrPhone' OR phone='$emailOrPhone' OR phone LIKE '%$emailOrPhone') AND password='$encryptedPassword'");
        
        $stmt->execute();
        $result = new MySqliStmt($stmt);

	    while ($row = $result -> fetch_assoc()) {
            if(sizeof($row)>0){
                array_push($user, $row);
            }
        }
        
        $stmt->close();
        return $user;
    }

     public function createUser($user_data)
    {
                    
        $name=$user_data['name'];
        $phone=$user_data['phone'];
        $firebase_uid = $user_data['firebase_uid'];
        $unique_id = $user_data['unique_id'];
        $email=$user_data['email'];
        $password=$user_data['password'];
        $dob=$user_data['dob'];
        $reg_number=$user_data['reg_number'];
        $account_status = $user_data['account_status'];

        $now = new DateTime();
        $created_at= $now->format("d M, Y h:i A");
        $updated_at=$created_at;
        
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
        
        $stmt = $this->con->prepare("INSERT INTO users (name,phone,email,firebase_uid,unique_id,password,dob,reg_number,account_status,created_at,updated_at) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
        $stmt->bind_param("ssssssssiss",$name, $phone, $email,$firebase_uid,$unique_id,$encryptedPassword,$dob,$reg_number,$account_status,$created_at,$updated_at);
        
        if ($stmt->execute()){
            $id=$stmt->insert_id;
            $stmt->close();
            return $id;
        }
        $stmt->close();
        return "error";
    }

    public function createUserWithImage($file, $extension,$user_data)
    {

        $name=$user_data['name'];
        $phone=$user_data['phone'];
        $email=$user_data['email'];
        $password=$user_data['password'];
        $firebase_uid = $user_data['firebase_uid'];
        $unique_id = $user_data['unique_id'];
        $dob=$user_data['dob'];
        $reg_number=$user_data['reg_number'];
        $account_status = $user_data['account_status'];

        $now = new DateTime();
        $created_at= $now->format("d M, Y h:i A");
        $updated_at=$created_at;
        
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

        $user_image = round(microtime(true) * 1000) . '.' . $extension;
        $filedest = dirname(__FILE__) . IMAGES_UPLOAD_PATH . $user_image;

        move_uploaded_file($file, $filedest);

        $image_url = (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? "https" : "http") . "://$_SERVER[HTTP_HOST]"."/api/v1/uploads/images/".$user_image;

        $stmt = $this->con->prepare("INSERT INTO users (name,phone,email,password,firebase_uid,unique_id,dob,reg_number,image_url,account_status,created_at,updated_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?) ");
        $stmt->bind_param("sssssssssiss",$name, $phone, $email,$encryptedPassword,$firebase_uid,$unique_id,$dob,$reg_number,$image_url,$account_status,$created_at,$updated_at);

        if ($stmt->execute()){
            $id=$stmt->insert_id;
            $stmt->close();
            return $id;
        }
        $stmt->close();
        return "error";
    }
    
    public function getUserDetails($emailOrPhone)
    {
        $user = array();
       
        $stmt = $this->con->prepare("SELECT * FROM users WHERE  email='$emailOrPhone' OR phone='$emailOrPhone' OR phone LIKE '%$emailOrPhone'");
        
        $stmt->execute();
        $result = new MySqliStmt($stmt);
        while ($row = $result -> fetch_assoc()) { 
	    $user=$row; 
        }
        
       
        $stmt->close();
        return $user;
    }
    
    public function getAllUsers()
    {
        $stmt = $this->con->prepare("SELECT * FROM users ORDER BY id DESC");
        $stmt->execute();
        //$result = $stmt -> get_result();

        $users = array();

        //if get_result() doesn't work
        $result = new MySqliStmt($stmt);
        while ($row = $result -> fetch_assoc()) {

            if(sizeof($row)>0){
                array_push($users, $row);
            }
        }
        $stmt->close();

        return $users;
    }
    
    public function updateUser($user_data)
    {

        $id=$user_data['id'];
        $name=$user_data['name'];
        $phone=$user_data['phone'];
        $email=$user_data['email'];
        $password=$user_data['password'];
        $dob=$user_data['dob'];
        $reg_number=$user_data['reg_number'];
        $image_url=$user_data['image_url'];
        $account_status=$user_data['image_url'];

        $now = new DateTime();
        $updated_at= $now->format("d M, Y h:i A");
        
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

        $stmt=null;
        if ($password!=null) {
            $stmt = $this->con->prepare("UPDATE users SET name=?,phone=?,email=?, password=?, dob=?, reg_number=?, image_url=?,updated_at=? WHERE id=?");
            $stmt->bind_param("ssssssssi", $name, $phone, $email, $encryptedPassword, $dob, $reg_number, $image_url, $updated_at, $id);
        }else{
            $stmt = $this->con->prepare("UPDATE users SET name=?,phone=?,email=?, dob=?, reg_number=?, image_url=?,updated_at=? WHERE id=?");
            $stmt->bind_param("sssssssi", $name, $phone, $email, $dob, $reg_number, $image_url, $updated_at, $id);
        }
        $stmt->execute();

        $affected_rows=$stmt->affected_rows;
        $stmt->close();
        if ($affected_rows>0){
            return "updated";
        }
 
        return "error";
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
        
        $stmt = $this->con->prepare("UPDATE users SET password=? WHERE id=?");
        $stmt->bind_param("si", $encryptedPassword, $id);
        $stmt->execute();

        $affected_rows = $stmt->affected_rows;
        $stmt->close();
        if ($affected_rows > 0) {
            return "updated";
        }

        return "error";
    }
    
    public function updateUniqueId($id, $unique_id)
    {

        $now = new DateTime();
        $updated_at= $now->format("d M, Y h:i A");

    
        $stmt  = $this->con->prepare("UPDATE users SET unique_id=?, updated_at=? WHERE id=?");
        
        $stmt->bind_param("ssi",$unique_id,$updated_at,$id);
        
        $stmt->execute();

        $affected_rows=$stmt->affected_rows;
        $stmt->close();
        if ($affected_rows>0){
            return "updated";
        }
 
        return "error";
    }
    
    public function updateAccountStatusValue($id, $account_status)
    {

        $now = new DateTime();
        $updated_at= $now->format("d M, Y h:i A");

    
        $stmt  = $this->con->prepare("UPDATE users SET account_status=?, updated_at=? WHERE id=?");
        
        $stmt->bind_param("isi",$account_status,$updated_at,$id);
        
        $stmt->execute();

        $affected_rows=$stmt->affected_rows;
        $stmt->close();
        if ($affected_rows>0){
            return "updated";
        }
 
        return "error";
    }

    public function updateAccountStatus($id, $account_status, $account_valid_from, $account_valid_until)
    {

        $now = new DateTime();
        $updated_at= $now->format("d M, Y h:i A");

        
        $stmt = $this->con->prepare("UPDATE users SET account_status=?, account_valid_from=?,account_valid_until=?,updated_at=? WHERE id=?");
        $stmt->bind_param("isssi", $account_status, $account_valid_from, $account_valid_until, $updated_at, $id);
        
        $stmt->execute();

        $affected_rows=$stmt->affected_rows;
        $stmt->close();
        if ($affected_rows>0){
            return "updated";
        }
 
        return "error";
    }

    public function updateUserWithFile($file, $extension,$user_data)
    {

        $id=$user_data['id'];
        $name=$user_data['name'];
        $phone=$user_data['phone'];
        $email=$user_data['email'];
        $password=$user_data['password'];
        $dob=$user_data['dob'];
        $reg_number=$user_data['reg_number'];
        $image_url=$user_data['image_url'];

        $now = new DateTime();
        $created_at= $now->format("d M, Y h:i A");
        $updated_at=$created_at;
        
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

        $user_image = round(microtime(true) * 1000) . '.' . $extension;
        $filedest = dirname(__FILE__) . IMAGES_UPLOAD_PATH . $user_image;
        move_uploaded_file($file, $filedest);
        $image_url = (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? "https" : "http") . "://$_SERVER[HTTP_HOST]"."/api/v1/uploads/images/".$user_image;


        $old_image_url=$user_data['image_url'];
        $parts=explode("images/",$old_image_url);
        $image=$parts[1];
        unlink('uploads/images/'.$image);
        
        $stmt=null;
        if ($password!=null) {
            $stmt = $this->con->prepare("UPDATE users SET name=?,phone=?,email=?, password=?, dob=?, reg_number=?, image_url=?,updated_at=? WHERE id=?");
            $stmt->bind_param("sssssssssi",$name, $phone, $email,$encryptedPassword,$dob,$reg_number, $image_url, $created_at,$updated_at,$id);
        }else{
            $stmt = $this->con->prepare("UPDATE users SET name=?,phone=?,email=?, dob=?, reg_number=?, image_url=?,updated_at=? WHERE id=?");
            $stmt->bind_param("ssssssssi",$name, $phone, $email,$dob,$reg_number, $image_url, $created_at,$updated_at,$id);
        }

        $stmt->execute();
        $affected_rows=$stmt->affected_rows;
        $stmt->close();
        if ($affected_rows>0){
            return "updated";
        }

        return "error";
    }
    
    public function updateProfileImage($id, $file, $extension)
    {

        $now = new DateTime();
        $created_at= $now->format("d M, Y h:i A");
        $updated_at=$created_at;

        $user_image = round(microtime(true) * 1000) . '.' . $extension;
        $filedest = dirname(__FILE__) . IMAGES_UPLOAD_PATH . $user_image;
        move_uploaded_file($file, $filedest);
        $image_url = (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? "https" : "http") . "://$_SERVER[HTTP_HOST]"."/api/v1/uploads/images/".$user_image;

        $stmt = $this->con->prepare("UPDATE users SET image_url=?, updated_at=? WHERE id=?");
        $stmt->bind_param("ssi",$image_url,$updated_at,$id);

        $stmt->execute();
        $affected_rows=$stmt->affected_rows;
        $stmt->close();
        if ($affected_rows>0){
            return "updated";
        }

        return "error";
    }
    
}

   

?>