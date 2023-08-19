<?php

class MySqliStmt {
    private $stmt;
    private $row;

    public function __construct($stmt){
        $this->stmt = $stmt;
        $md = $stmt->result_metadata();
        $params = array();
        while($field = $md->fetch_field()) {
            $params[] = &$this->row[$field->name];
        }
        call_user_func_array(array($stmt, 'bind_result'), $params) or die('Sql Error');
    }

    public function fetch_assoc(){
        if($this->stmt->fetch()){
            $result = array();
            foreach($this->row as $k => $v){
                $result[$k] = $v;
            }
            return $result;
        }else{
            return false;
        }
    }

    public function free(){
        $this->stmt->close();
    }
}
