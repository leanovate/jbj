<?php
// Turkish Language Translation(s)
  // (c) 2004 Alexander Palmo, apalmo <at> bigevilbrain <dot> com
  // (c)  Edits: 2007 Ferdi KIZIL, rhythm80 <at> hotmail <dot> com

  // Simple PHP Version: 0.5.0.1
  // Language Version:   0.5.0.1

  function sb_language( $page ="") {
    global $lang_string;

    // Language: Turkce
    $lang_string['language'] = 'Turkce';
    $lang_string['locale'] = array('tr_TR', 'tr');
    $lang_string['rss_locale'] = 'tr_TR'; // New 0.4.8

     // ISO Charset: ISO-8859-1
     $lang_string['html_charset'] = 'ISO-8859-0';
     $lang_string['php_charset'] = 'ISO-8859-9';
     setlocale(LC_TIME, $lang_string['locale'] ); 
		
  }
?>
