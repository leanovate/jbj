<?php

  // The Simple PHP Blog is released under the GNU Public License.
  //
  // You are free to use and modify the Simple PHP Blog. All changes
  // must be uploaded to SourceForge.net under Simple PHP Blog or
  // emailed to apalmo <at> bigevilbrain <dot> com

  // comment_to_array ( $entryFile )
  // read_comments ( $y, $m, $entry, $logged_in )
  // read_unmodded_comments ( $logged_in )
  // get_unmodded_count ( $logged_in )
  // blog_comment_listing ()
  // get_entry_unmodded_count ( $y, $m, $entry)
  // are_comments_expired ($month, $day, $year)  
  // sb_display_email ($email)
  // sb_str_to_ascii ($str)
  // write_comment ( $y, $m, $entry, $comment_name, $comment_email, $comment_url, $comment_text, $user_ip, $hold_flag='', $comment_date=null )
  // set_comment_holdflag ( $filename, $hold_flag='')
  // delete_comment ( $filepath )

  // ----------------------
  // Blog Comment Functions
  // ----------------------

  function comment_to_array ( $entryFile ) {
    // Reads a blog entry and returns an key/value pair array.
    //
    // Returns false on fail...
    global $sb_info;
    $comment_entry_data = array();

    $str = sb_read_file( $entryFile );
    $exploded_array = explode( '|', $str );

    if ( count( $exploded_array ) > 1 ) {
      if ( count( $exploded_array ) <= 5 ) {
        // Old List Format: name, date, content, email, url
        // This function is for campatibility with older versions of sphpblog
        $comment_entry_data[ 'VERSION' ]  = $sb_info[ 'version' ];
        $comment_entry_data[ 'NAME' ]  = $exploded_array[0];
        $comment_entry_data[ 'DATE' ]  = $exploded_array[1];
        $comment_entry_data[ 'CONTENT' ]  = $exploded_array[2];
        if ( count( $exploded_array ) > 3) {
          $comment_entry_data[ 'EMAIL' ]  = $exploded_array[3];
        }
        if ( count( $exploded_array ) > 4) {
          $comment_entry_data[ 'URL' ]  = $exploded_array[4];
        }

      } else {
        // New Format: key/value pairs
        // VERSION, NAME, DATE, CONTENT, EMAIL, URL, FLAG

        $comment_entry_data = explode_with_keys( $exploded_array );
      }
      return( $comment_entry_data );
    } else {
      // Exploded array only contained 1 item, so something is wrong...
      return( false );
    }
  }

  function read_comments ( $y, $m, $entry, $logged_in ) {
    global $blog_content, $blog_config, $user_colors;

    //$filename = CONTENT_DIR.$y.'/'.$m.'/'.$entry;

    //$blog_content = read_entry_from_file( $filename );
    //$blog_content = replace_more_tag ( $blog_content , true, '' );

    // Comments
    $basedir = CONTENT_DIR;
    $dir = $basedir.$y.'/'.$m.'/'.$entry.'/comments/';
    $file_array = sb_folder_listing( $dir, array( '.txt', '.gz' ) );
    if ( $blog_config->getTag('BLOG_COMMENT_ORDER') == 'new_to_old' ) {
      $file_array = array_reverse( $file_array );
    }

    // View Count
    if ( $logged_in == false ) {
      $view_counter = 1;
      $view_array = sb_folder_listing( $dir.'../', array( '.txt' ) );
      // $view_array = sb_folder_listing( $basedir.$y.'/'.$m.'/'.$entry.'../', array( '.txt' ) ); // http://forums.simplephpblog.com/viewtopic.php?t=2821
      for ( $i = 0; $i < count( $view_array ); $i++ ) {
        if ( $view_array[$i] === 'view_counter.txt' ) {
          $view_counter = intval( sb_read_file( $dir . '../' . $view_array[$i] ) ) + 1;
        }
      }

      // Create one regardless - this is so it works when comments are turned off
      // Make sure that the folder exists for old users
      if (!file_exists($dir)) {
        @mkdir($dir, BLOG_MASK, TRUE);
      }
      sb_write_file( $dir . '../view_counter.txt' , $view_counter );
    }

    $contents = array();
    for ( $i = 0; $i < count( $file_array ); $i++ ) {
      if ( $file_array[$i] !== 'rating.txt' ) {
        array_push( $contents, array( 'path' => ( $dir . $file_array[$i] ), 'entry' => $file_array[$i] ) );
      }
    }

    if ( $contents ) {
      // Display comments Oldest to Newest to. Oldest Comments will be at the top of the page.
      for ( $i = 0; $i <= count( $contents ) - 1; $i++ ) {

        $comment_entry_data = comment_to_array( $contents[$i][ 'path' ] );

        $entry_array = array();

        // I will probably move this into it's own key/value
        // pairs instead of sticking it in the subject line.
        $comment_subject = $comment_entry_data[ 'NAME' ];

        if ( isset( $comment_entry_data[ 'URL' ] ) ) {
          if ( strpos( strtolower( $comment_entry_data[ 'URL' ] ), 'http://' ) === false ) {
            $website = '[url=http://'.($comment_entry_data[ 'URL' ]).' ]'.($comment_entry_data[ 'URL' ]).'[/url]';
          } else {
            $website = '[url='.($comment_entry_data[ 'URL' ]).' ]'.($comment_entry_data[ 'URL' ]).'[/url]';
          }
          $entry_array[ 'website' ] = blog_to_html( $website, true, false, true );
        }
        
        if ( isset( $comment_entry_data[ 'EMAIL' ] ) ) {
          $entry_array[ 'email' ] = sb_display_email( $comment_entry_data[ 'EMAIL' ] );
        }

        // blog_to_html( $str, $comment_mode, $strip_all_tags, $add_no_follow=false, $emoticon_replace=false )
        $entry_array[ 'id' ] = $entry . '_' . sb_strip_extension( $contents[$i][ 'entry' ] );
        $entry_array[ 'subject' ] = blog_to_html( $comment_subject, true, false, true );
        $entry_array[ 'date' ] = blog_to_html( format_date( $comment_entry_data[ 'DATE' ] ), true, false );
        $entry_array[ 'entry' ] = blog_to_html( $comment_entry_data[ 'CONTENT' ], true, false, true, true ) . '<br />';
        $entry_array[ 'modflag' ] = blog_to_html( $comment_entry_data[ 'MODERATIONFLAG' ], true,  false, true );
        $entry_array[ 'logged_in' ] = $logged_in;

        // Author
        $admin = $_SESSION[ 'fulladmin' ];
        if( (( $logged_in == true) and ( $admin == 'no' ) and ( CheckUserSecurity( $_SESSION[ 'username' ], 'MOD' ) == true ) ) or
            (( $logged_in == true) and ( $admin == 'yes' )))
        {
          $entry_array[ 'delete' ][ 'name' ] = _sb('delete_btn');
          $entry_array[ 'delete' ][ 'url' ] = 'comment_delete_cgi.php?y='.$y.'&amp;m='.$m.'&amp;entry='.$entry.'&amp;comment=' . ( $contents[$i][ 'entry' ] );

          if ( array_key_exists( 'IP-ADDRESS', $comment_entry_data ) ) {
            $entry_array[ 'ban' ][ 'name' ] = _sb('ban_btn');
            $entry_array[ 'ban' ][ 'url' ] = 'comment_ban_cgi.php?ban=' . $comment_entry_data[ 'IP-ADDRESS' ] .'&amp;y='.$y.'&amp;m='.$m.'&amp;entry='.$entry.'&amp;comment=' . ( $contents[$i][ 'entry' ] );
          }
        }

        $entry_array[ 'count' ] = $i;
        $entry_array[ 'maxcount' ] = count( $contents ) - 1;

        // New 0.4.8
        if ( array_key_exists( 'IP-ADDRESS', $comment_entry_data ) ) {
          $entry_array[ 'ip-address' ] = $comment_entry_data[ 'IP-ADDRESS' ];
        }

        // Check if moderation is on - if it is, then don't show this item
        // unless the user is logged in or the item is marked as 'H'
        if (( $blog_config->getTag('BLOG_COMMENTS_MODERATION') == 1 ) && ( $entry_array[ 'modflag' ] == 'H' ) && ( $logged_in != 1 )) {
          $blog_content = $blog_content;
        } else {
          $blog_content  .= theme_commententry( $entry_array );
        }
      }
    }

    return $blog_content;
  }

  function read_unmodded_comments ( $logged_in ) {
    global $blog_config;
    
    if ( $blog_config->getTag('BLOG_ENABLE_COMMENTS') != true ) {
      return('Comments are not enabled. Check the Preferences page.');
    }

    // To avoid server overload
    sleep(1);
    $output_str = '';
    $entry_file_array = blog_entry_listing();
    $results = 0;

    // Loop through entry files
    for ( $i = 0; $i < count( $entry_file_array ); $i++ ) {
      list( $entry_filename, $year_dir, $month_dir ) = explode( '|', $entry_file_array[ $i ] );
      $contents = sb_read_file( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . $entry_filename );
      $j = 0;
      $blog_entry_data = blog_entry_to_array( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . $entry_filename );
      // Search Comments
      $comment_file_array = sb_folder_listing( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . sb_strip_extension( $entry_filename ) . '/comments/', array( '.txt', '.gz' ) );

      for ( $k = 0; $k < count( $comment_file_array ); $k++ ) {
        $comment_filename =  $comment_file_array[ $k ];
        //We only want to search inside comments, not the counter
        if ( strpos($comment_filename, 'comment') === 0 ) {
          // $contents_comment = sb_read_file( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . sb_strip_extension( $entry_filename ) . '/comments/' . $comment_filename );
          $comment_entry_data = comment_to_array( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . sb_strip_extension( $entry_filename ) . '/comments/' . $comment_filename );

          // check to see if our comment is on hold right now (from a mod point of view)
          if ( $comment_entry_data[ 'MODERATIONFLAG' ] == 'H') {
            global $theme_vars;
            $results++;
              $output_str .= '<b>' . _sb('enteredby') . $comment_entry_data[ 'NAME' ]  . '</b><br />';
              $output_str .= _sb('entrydate') . format_date( $comment_entry_data[ 'DATE' ] ) . '<br />';
              $output_str .= _sb('blogentrytitle') . '<a href="comments.php?y=' . $year_dir . '&amp;m=' . $month_dir . '&amp;entry=' . sb_strip_extension( $entry_filename ) . '" title="' . format_date( $comment_entry_data[ 'DATE' ] ) . '">' . $blog_entry_data[ 'SUBJECT' ] . '</a><br />';
              $output_str .= _sb('enteredcontent') . $comment_entry_data[ 'CONTENT' ] . '<br />';
              $output_str .= '<a href="comment_approve_cgi.php?y=' . $year_dir . '&amp;m=' . $month_dir . '&amp;entry=' . sb_strip_extension( $entry_filename ) . '&amp;comment=' . $comment_filename . '" title="' . format_date( $comment_entry_data[ 'DATE' ] ) . '">' . _sb('mod_approve') . '</a>';
              $output_str .= '<a href="comment_delete_cgi.php?y=' . $year_dir . '&amp;m=' . $month_dir . '&amp;entry=' . sb_strip_extension( $entry_filename ) . '&amp;comment=' . $comment_filename . '&amp;sourcepage=m" title="' . format_date( $comment_entry_data[ 'DATE' ] ) . '">' . _sb('mod_delete') . '</a><br /><br />';
          }
        }
      }
    }
    $output_str .= '<b>' . $results . _sb('totalunmodded') . '</b>';

    return ( $output_str );
  }
  
  function get_unmodded_count( $logged_in ) {
    $comment_array = blog_comment_listing();
    
    $results = 0;
    for ( $i = 0; $i < count($comment_array); $i++ ) {
      // Get data
      list( $entry_filename, $year_dir, $month_dir, $comment_filename, $moderation_flag ) = explode( '|', $comment_array[ $i ] );
      
      if ( $moderation_flag == 'H') {
        // Comment is being held
        $results++;
      }
    }
    
    return( $results );
  }
  
  function blog_comment_listing () {
    // Return array of all the blog comment files.
    
    // Load cached file array.
    $filename = CONFIG_DIR.'~blog_comment_listing.tmp';
    $comment_array = sb_read_file( $filename );
    if ( $comment_array != NULL ) {
      // Using cached array.
      // echo("USING CACHED ARRAY");
      $comment_array = unserialize( $comment_array );
      rsort( $comment_array ); // Sort array newest to oldest
    } else {
      // Rebuild array.
      // echo("REBUILDING ARRAY");
      // sleep(1); // To avoid server overload
      
      $comment_array = array();
      $comment_array_time = array();
      $entry_file_array = blog_entry_listing();
  
      // Loop through entry files
      for ( $i = 0; $i < count( $entry_file_array ); $i++ ) {
        list( $entry_filename, $year_dir, $month_dir ) = explode( '|', $entry_file_array[ $i ] );
        // $contents = sb_read_file( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . $entry_filename );
        // $blog_entry_data = blog_entry_to_array( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . $entry_filename );
        
        // Get files in the comments folder.
        $comment_file_array = sb_folder_listing( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . sb_strip_extension( $entry_filename ) . '/comments/', array( '.txt', '.gz' ) );

        // Look through comments.
        for ( $k = 0; $k < count( $comment_file_array ); $k++ ) {
          $comment_filename =  $comment_file_array[ $k ];
          // We only want to search inside comments, not the counter file.
          if ( strpos($comment_filename, 'comment') === 0 ) {
            $comment_entry_data = comment_to_array( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . sb_strip_extension( $entry_filename ) . '/comments/' . $comment_filename );
            
            array_push( $comment_array_time, $comment_entry_data[ 'DATE' ] );
            array_push( $comment_array, implode( '|', array( $entry_filename, $year_dir, $month_dir, $comment_filename, $comment_entry_data[ 'MODERATIONFLAG' ] ) ) );
          }
        }     
      }
      
      // this needs to be a 2D array sort!
      array_multisort($comment_array_time, SORT_DESC, $comment_array);

      // Save array if not empty
      if ( count( $comment_array )>0 ) {
        sb_write_file( $filename, serialize( $comment_array ) );
      }
    }
    
    /*
    echo("<pre>");
    print_r( $comment_array );
    echo("</pre>");
    */
    
    return( $comment_array );
  }
  
  function get_entry_unmodded_count ( $y, $m, $entry ) {
    $comment_array = blog_comment_listing();
    
    $results = 0;
    for ( $i = 0; $i < count($comment_array); $i++ ) {
      // Get data
      list( $entry_filename, $year_dir, $month_dir, $comment_filename, $moderation_flag ) = explode( '|', $comment_array[ $i ] );

      if ( $entry == sb_strip_extension( $entry_filename ) ) {
        if ( $moderation_flag == 'H') {
          // Comment is being held
          $results++;
        }
      }
    }
    
    return( $results );
  }

  function check_for_duplicate ( $y, $m, $entry, $newContent ) {
    // Comments
    $basedir = CONTENT_DIR;
    $dir = $basedir.$y.'/'.$m.'/'.$entry.'/comments/';
    $file_array = sb_folder_listing( $dir, array( '.txt', '.gz' ) );

    $contents = array();
    for ( $i = 0; $i < count( $file_array ); $i++ ) {
      if ( $file_array[$i] !== 'rating.txt' ) {
        array_push( $contents, array( 'path' => ( $dir . $file_array[$i] ), 'entry' => $file_array[$i] ) );
      }
    }

    $is_duplicate = false;
    if ( $contents ) {
      for ( $i = 0; $i <= count( $contents ) - 1; $i++ ) {
        $comment_entry_data = comment_to_array( $contents[$i][ 'path' ] );
        //
        // $comment_entry_data[ 'VERSION' ]
        // $comment_entry_data[ 'NAME' ]
        // $comment_entry_data[ 'DATE' ]
        // $comment_entry_data[ 'CONTENT' ]
        // $comment_entry_data[ 'EMAIL' ] <-- Optional
        // $comment_entry_data[ 'URL' ] <-- Optional
        //
        if ( $comment_entry_data[ 'CONTENT' ] == $newContent ) {
          $is_duplicate == true;
          break;
        }
      }
    }

    return $is_duplicate;
  }

  function sb_display_email ($email) {
    $arr = explode( '@', $email );
    if ( $arr === false ) {
      $arr = Array( $email );
    }

    $htmlstr = '<script type="text/javascript">';
    //$htmlstr .= '<!--';
    $javastr = '';
    for ( $i=0; $i<count($arr); $i++ ) {
      $strord = sb_str_to_ascii( $arr[$i] );
      $htmlstr .= 'str'.$i." = ('".$strord."');";
      $javastr .= 'str'.$i.'+';
      if ( $i < count($arr) - 1 ) {
        $javastr .= "'&lt;at&gt;'+";
      }
    }
    $htmlstr .= "document.write('<a href=\"mailto:'+".$javastr."'\">'+".$javastr."'</a>');";
    //$htmlstr .= "//-->";
    $htmlstr .= '</script>';

    return($htmlstr);
  }

  function are_comments_expired ($month, $day, $year) {
    // Finds out if the comments are expired based on the setting
    // in the preferences
    global $blog_config;

    $tmp_expiry = intval( $blog_config->getTag('BLOG_COMMENT_DAYS_EXPIRY') );
    if ( $tmp_expiry < 1 ) {
      return ( false );
    } else {
      $blog_entry_date = mktime(0,0,0,$month,$day,$year);
      $todays_date = mktime(0,0,0,date('m'),date('d'),date('Y'));
      $days_elapsed = Round((($todays_date - $blog_entry_date)/86400), 0) ;
      $tmp_expiry = intval( $blog_config->getTag('BLOG_COMMENT_DAYS_EXPIRY') );
      if ( ($days_elapsed) >= $tmp_expiry ) {
        return ( true );
      } else {
        return ( false );
      }
    }
  }

  function sb_str_to_ascii ($str) {
    // Converts a string to ASCII HEX code. This is used for email obfuscation.
    //
    $res='';
    for ( $j=0; $j<strlen($str); $j++) {
      $res.='&#' . ord(substr($str, $j, 1)) . ';';
    }
    return($res);
  }

  function write_comment ( $y, $m, $entry, $comment_name, $comment_email, $comment_url, $comment_text, $user_ip, $hold_flag='', $comment_date=null ) {
    // Save new entry or update old entry
    global $blog_config, $sb_info;
    
    sb_delete_file( CONFIG_DIR.'~blog_comment_listing.tmp' ); // Delete comment array cache

    // We're going to assume that the y and m directories exist...
    $basedir = CONTENT_DIR;
    $dir = $basedir.$y.'/'.$m.'/'.$entry;

    if (!file_exists($dir)) {
      $oldumask = umask(0);
      $ok = mkdir($dir, BLOG_MASK );
      umask($oldumask);
      if (!$ok) {
        // There is a bug in some versions of PHP that will
        // cause mkdir to fail if there is a trailing "/".
        //
        // Thanks to Matt - http://agent.chaosnet.org
        return ( $dir );
      }
    }

    $dir .= '/comments';

    if (!file_exists($dir)) {
      $oldumask = umask(0);
      $ok = mkdir($dir, BLOG_MASK );
      umask($oldumask);
      if (!$ok) {
        // There was a problem creating the directory
        return ( $dir );
      }
    }

    $dir  .= '/';

    if (!isset($comment_date)) {
      $comment_date = time();
    }

    $stamp = date('ymd-His');
    $entryFile = $dir.'comment'.$stamp.'.txt';

    // Save the file
    $save_data = array();
    $save_data[ 'VERSION' ] = $sb_info[ 'version' ];
    $save_data[ 'NAME' ] = clean_post_text( $comment_name );
    $save_data[ 'DATE' ] = $comment_date;
    $save_data[ 'CONTENT' ] = sb_parse_url( clean_post_text( $comment_text ) );
    if ( $comment_email != '' ) {
      $save_data[ 'EMAIL' ] = clean_post_text( $comment_email );
    }
    if ( $comment_url != '' ) {
      $save_data[ 'URL' ] = clean_post_text( $comment_url );
    }
    $save_data[ 'IP-ADDRESS' ] = $user_ip; // New 0.4.8
    $save_data[ 'MODERATIONFLAG' ] = $hold_flag;

    // Implode the array
    $str = implode_with_keys( $save_data );

    // Save the file
    $result = sb_write_file( $entryFile, $str );

    if ( $result ) {

      if ( $blog_config->getTag('BLOG_EMAIL_NOTIFICATION') ) {
        // Send Email Notification:

        $client_ip_local = getIP();

        $subject= _sb('commentposted') . ' ' . $blog_config->getTag('BLOG_TITLE');
        $body='<b>' . _sb('name') . '</b> ' . $save_data[ 'NAME' ] . '<br />';
        $body .= '<b>' . _sb('IPAddress') . '</b> ' . $client_ip_local . ' (' . @gethostbyaddr($client_ip_local) .')<br />';
        $body .= '<b>' . _sb('useragent') . '</b> ' . $_SERVER[ 'HTTP_USER_AGENT' ] . '<br />';
        if ( array_key_exists( 'EMAIL', $save_data ) ) {
          $body .= "<b>" . _sb('email') . "</b> <a href=\"mailto:" . $save_data[ "EMAIL" ] . "\">" . $save_data[ "EMAIL" ] . "</a><br />\n";
        }
        if ( array_key_exists( 'URL', $save_data ) ) {
          $body .= "<b>" . _sb('homepage') . "</b> <a href=\"" . $save_data[ "URL" ] . "\">" . $save_data[ "URL" ] . "</a><br />\n";
        }
        $body .= "<br />\n";
        $body .= "<b>" . _sb('comment') . "</b><br />\n";

        $port = ':' . $_SERVER[ 'SERVER_PORT'];
        if ($port == ':80') {
          $port = '';
        }       
        if ( ( dirname($_SERVER[ 'PHP_SELF' ]) == '\\' || dirname($_SERVER[ 'PHP_SELF' ]) == '/' ) ) {
          // Hosted at root.
          $base_url = 'http://'.$_SERVER[ 'HTTP_HOST' ].$port.'/';
        } else {
          // Hosted in sub-directory.
          $base_url = 'http://'.$_SERVER[ 'HTTP_HOST' ].$port.dirname($_SERVER[ 'PHP_SELF' ]).'/';
        }

        $body  .= '<a href="' . $base_url . 'comments.php?y=' . $y . '&amp;m=' . $m . '&amp;entry=' . $entry . '">' . $base_url . 'comments.php?y=' . $y . '&amp;m=' . $m . '&amp;entry=' . $entry . "</a><br />\n<br />\n";
        $body  .= sprintf( _sb('wrote'), format_date( $comment_date ), $comment_name, blog_to_html( $comment_text, true, false ) );
        $body  .= '<br /><br />';

        if ( $blog_config->getTag('BLOG_COMMENTS_MODERATION') ) {
          if ( $logged_in == false ) {
            $body  .= _sb('email_moderator') . "\n";
          }
        }

        // Send the Email
        if ( array_key_exists( 'EMAIL', $save_data ) ) {
          sb_mail( $save_data[ 'EMAIL' ], $blog_config->getTag('BLOG_EMAIL'), $subject, $body, false );
        } else {
          sb_mail( $blog_config->getTag('BLOG_EMAIL'), $blog_config->getTag('BLOG_EMAIL'), $subject, $body, false );
        }
      }

      return ( true );
    } else {
      // Error:
      // Probably couldn't create file...
      return ( $entryFile );
    }
  }

  function set_comment_holdflag ( $filename, $hold_flag='') {
    // Save new entry or update old entry
    global $blog_config, $sb_info;

    sb_delete_file( CONFIG_DIR.'~blog_comment_listing.tmp' );

    $comment_entry_data = comment_to_array( $filename );

    // Now update the flag
    $comment_entry_data[ 'MODERATIONFLAG' ] = $hold_flag;
    $str = implode_with_keys( $comment_entry_data );

    $result = sb_write_file( $filename, $str );

    return ( true );
  }

  function delete_comment ( $filepath ) {
    // Delete a comment. Also, delete the whole comment folder if it was the only comment.
	
    sb_delete_file( CONFIG_DIR.'~blog_comment_listing.tmp' ); // Delete comment array cache

    // Delete the comment file:
    $ok = sb_delete_file( $filepath ); // content/07/10/entry071016-093727/comments/comment071016-095416.txt.gz

    // Trim off filename and leave path to last directory.
    $dirpath = $filepath;
    
    $pos = strrpos( $dirpath, '/' );
    if ($pos !== false) {
      $dirpath = substr( $dirpath, 0, $pos ); // content/07/10/entry071016-093727/comments
      
      // Get listing of all comment files in folder.
      $file_array = sb_folder_listing( $dirpath . '/', array( '.txt', '.gz' ) );
      
      if ( count( $file_array ) == 0 ) {
        sb_delete_directory( $dirpath );
        
        // Delete the entry071016-093727 which contains the view_counter.txt file also
        $pos = strrpos( $dirpath, '/' );
        if ($pos !== false) {
          $dirpath = substr( $dirpath, 0, $pos ); // content/07/10/entry071016-093727
           
          sb_delete_directory( $dirpath );
        }
      }
    }

    return ( $ok );
  }
?>
