<?php

require_once dirname(__FILE__) . '/PaymentHandler.php';
require_once dirname(__FILE__) . '/UserHandler.php';

$response = array();

//for receiving object as request
$data = file_get_contents('php://input');
$payment_data = json_decode($data, true);

if (isset($_GET['call'])) {
    switch ($_GET['call']) {

        case "new":
            if (isset($payment_data['user_id'])) {

                $save = new PaymentHandler();
                $result = $save->createPayment($payment_data);


                if ($result == "ok") {
                    $response['error'] = false;
                    $response['response'] = 'Payment Successful!';
                } else {
                    $response['error'] = true;
                    $response['response'] = 'Nothing Changed!';
                }


            } else {
                $response['error'] = true;
                $response['response'] = 'Error, Please Try Again!';
            }
            
            break;
            
            case 'updatePayment':

            if ($_SERVER['REQUEST_METHOD'] == "POST") {

                $userId = $_POST['user_id'];
                $amount = $_POST['amount'];
                $method = $_POST['method'];
                $accountNumber = $_POST['account_number'];

                $payment_data = array("user_id" => $userId, "amount" => $amount, "method" => $method, "account_number" => $accountNumber);


                $save = new PaymentHandler();
                $result = $save->updatePayment($payment_data);
                
                $user = new UserHandler();
                $userResult = $user->updateAccountStatusValue($userId,2);

                if ($result == "ok") {
                    $response['error'] = false;
                    $response['response'] = 'Payment Updated Successfully!';
                } else {
                    $response['error'] = true;
                    $response['response'] = 'Something went wrong!';
                }


            } else {
                $response['error'] = true;
                $response['response'] = 'Error, Please Try Again!';
            }

            break;

    }
}

if (isset($_GET['get'])) {
    switch ($_GET['get']) {

        case 'allPayment':

            $db = new PaymentHandler();
            $result = $db->getAllPayment();

            $response = $result;

            break;

        case 'paymentByName':

            if (isset($_POST['name'])) {
                $name = $_POST['name'];
                $db = new PaymentHandler();
                $result = $db->getPaymentByName($name);

                $response = $result;
            }

            break;
    }
}

echo json_encode($response);

?>