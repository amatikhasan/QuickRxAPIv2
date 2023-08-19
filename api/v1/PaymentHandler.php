<?php

require_once dirname(__FILE__) . '/MySqliStmt.php';

class PaymentHandler{

    private $con;
    
    public function __construct(){
        require_once dirname(__FILE__) . '/DbConnect.php';
        date_default_timezone_set('Asia/Dhaka');

        $db = new DbConnect();
        $this->con = $db->connect();
    }

    public function createPayment($payment_data){
        $user_id=$payment_data['$user_id'];
        $amount=$payment_data['amount'];
        $method=$payment_data['method'];
        $account_number=$payment_data['$account_number'];
        $trx_id=$payment_data['$trx_id'];

        $now = new DateTime();
        $created_at= $now->format("d M, Y h:i A");
        $updated_at=$created_at;

        $stmt = $this->con->prepare("INSERT INTO payment (user_id,amount,method,account_number,trx_id,created_at,updated_at) VALUES (?,?,?,?,?,?,?) ");
        $stmt->bind_param("iisssss",$user_id,$amount,$method , $account_number, $trx_id,$created_at,$updated_at);

        if ($stmt->execute()){
            $id=$stmt->insert_id;
            return array("id"=>$id , "user_id"=>$user_id, "amount"=>$amount,"method"=>$method, "account_number"=>$account_number, "trx_id"=>$trx_id  ,"created_at"=>$created_at, "updated_at"=>$updated_at);
        }

        return "error";
    }
    
    public function updatePayment($payment_data){
        $user_id=$payment_data['user_id'];
        $amount=$payment_data['amount'];
        $method=$payment_data['method'];
        $account_number=$payment_data['account_number'];

        $now = new DateTime();
        $created_at= $now->format("d M, Y h:i A");
        $updated_at=$created_at;

        $stmt = $this->con->prepare("INSERT INTO payment (user_id,amount,method,account_number,created_at,updated_at) VALUES (?,?,?,?,?,?) ");
        $stmt->bind_param("iissss",$user_id,$amount,$method , $account_number,$created_at,$updated_at);

        if ($stmt->execute()){
            $stmt->close();
            return "ok";
        }

        return "error";
    }

    public function getAllPayment(){
        $stmt = $this->con->prepare("SELECT payment.*,users.name AS user_name FROM payment LEFT JOIN users ON payment.user_id=users.id ORDER BY payment.id DESC");
        $stmt->execute();
        //$result = $stmt -> get_result();

        $payments = array();

        //if get_result() doesn't work
        $result = new MySqliStmt($stmt);
        while ($row = $result -> fetch_assoc()) {

            if(sizeof($row)>0){
                array_push($payments, $row);
            }
        }
        $stmt->close();

        return $payments;
    }

}
