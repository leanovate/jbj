<?php

function sb_import_lang($name, $page="") {
  include("$name/strings.php");
  // call old init function here
  
  //sb_language( $page );

  global $lang_string;

  $handle = fopen(ROOT_DIR . "/languages/$name/strings.po", "r");
  $lastid = "";
  $idmode = TRUE;
  $strmode = FALSE;
  if ($handle) {
    while (($buffer = fgets($handle, 4096)) !== false) {
        //echo $buffer;
      if (preg_match("/msgid \"(.*)\"/", $buffer, $matches)) {
        $lastid = $matches[1];
        $strmode = FALSE;
        $idmode = TRUE;
      }
      if (preg_match("/msgstr \"(.*)\"/", $buffer, $matches)) {
        $lang_string[$lastid] = $matches[1];
        $strmode = TRUE;
        $idmode = FALSE;
      }
      if ($strmode AND preg_match("/^\"(.*)\"/", $buffer, $matches)) {
        $lang_string[$lastid] .= $matches[1];
      }
      if ($idmode AND preg_match("/^\"(.*)\"/", $buffer, $matches)) {
        $lastid .= $matches[1];
      }
    }
    fclose($handle);
  }
}

function _sb($text) {
    global $lang, $lang_string;
    if (!array_key_exists($text, $lang_string) OR empty($lang_string[$text])) {
	//print var_dump(debug_backtrace());
        trigger_error("Missing translation string in $lang '$text'\n");
        return $text;
    }
    return $lang_string[$text];
}

?>
