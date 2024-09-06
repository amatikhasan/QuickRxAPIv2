<?php

require_once dirname(__FILE__) . '/MySqliStmt.php';

class ApplicationHandler
{
 
    private $con;
 
    public function __construct()
    {
        require_once dirname(__FILE__) . '/DbConnect.php';
        date_default_timezone_set('Asia/Dhaka');
 
        $db = new DbConnect();
        $this->con = $db->connect();
        
    }

    public function getAppInfo()
    {
       
        //$info = array();
        
        $stmt = $this->con->prepare("SELECT * FROM app_info LIMIT 1");
        $stmt->execute();
        
        $result = new MySqliStmt($stmt);
        while ($row = $result -> fetch_assoc()) { 
	    $info=$row; 
        }
        $stmt->close();
        return $info;
    }

    public function updateAppInfo($id,$subscription_fee,$account_number, $hotline, $facebook_link, $about_us, $terms)
    {
        $now = new DateTime();
        $created_at= $now->format("d M, Y h:i A");
        $updated_at=$created_at;
        
        $stmt = $this->con->prepare("UPDATE app_info SET subscription_fee=?, account_number=?,hotline=?,facebook_link=?,about_us=?, terms_and_condition=?, updated_at=?");
        $stmt->bind_param("issssss",$subscription_fee,$account_number,$hotline,$facebook_link,$about_us,$terms,$updated_at);
        $stmt->execute();
        $affected_rows=$stmt->affected_rows;
        $stmt->close();
        if ($affected_rows>0){
            return "updated";
        }
 
        return "error";
    }
    
    public function createFeedback($user_id, $feedback)
    {
        $now = new DateTime();
        $created_at= $now->format("d M, Y h:i A");
        $updated_at=$created_at;
        
        $stmt = $this->con->prepare("INSERT INTO feedback (user_id,feedback,created_at,updated_at) VALUES (?,?,?,?)");
        $stmt->bind_param("isss",$user_id,$feedback,$created_at,$updated_at);
        $stmt->execute();
        $affected_rows=$stmt->affected_rows;
        $stmt->close();
        if ($affected_rows>0){
            return "ok";
        }
 
        return "error";
    }
    
    public function getFeedback()
    {
        $feedback = array();
        $stmt = $this->con->prepare("SELECT feedback.*,users.name AS user_name FROM feedback LEFT JOIN users ON feedback.user_id=users.id ORDER BY feedback.id DESC");
        $stmt->execute();
        //$result = $stmt -> get_result();
         
        //if get_result() doesn't work
        $result = new MySqliStmt($stmt);
        while ($row = $result -> fetch_assoc()) { 
	    if(sizeof($row)>0){
	            array_push($feedback, $row);
	        }
        }
        $stmt->close();
        
        return $feedback;
    }
    
}

?>