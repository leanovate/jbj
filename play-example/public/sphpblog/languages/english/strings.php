<?php
  // English Language File
  // (c) 2004 Alexander Palmo, apalmo <at> bigevilbrain <dot> com
  // Edits: 2007 Bill Bateman, Alexander Palmo

  // Simple PHP Version: 0.5.0.1
  // Language Version:   0.5.0.1

  function sb_language( $page ="") {
    // Language: English
    $lang_string['language'] = 'english';
    $lang_string['locale'] = array('en_US', 'us');
    $lang_string['rss_locale'] = 'en-US'; // New 0.4.8

    // ISO Charset: ISO-8859-1
    $lang_string['html_charset'] = 'UTF-8';
    $lang_string['php_charset'] = 'UTF-8';
    setlocale( LC_TIME, $lang_string['locale'] );

 }
?>
