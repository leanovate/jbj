<?php
  // English Language File
  // (c) 2004 Alexander Palmo, apalmo <at> bigevilbrain <dot> com
  // Edits: 2007 Bill Bateman, Alexander Palmo

  // Simple PHP Version: 0.5.0.1
  // Language Version:   0.5.0.1

  function sb_language( $page ="") {
    global $lang_string;

    // Language: English
    $lang_string['language'] = 'english';
    $lang_string['locale'] = array('pt_BR', 'br');
    $lang_string['rss_locale'] = 'pt-BR'; // New 0.4.8

    // ISO Charset: ISO-8859-1
    $lang_string['html_charset'] = 'ISO-8859-1';
    $lang_string['php_charset'] = 'ISO-8859-1';
    setlocale( LC_TIME, $lang_string['locale'] );

  }
?>

 	  	 
