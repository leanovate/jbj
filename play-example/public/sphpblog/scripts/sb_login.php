<?php
  // The Simple PHP Blog is released under the GNU Public License.
  //
  // You are free to use and modify the Simple PHP Blog. All changes
  // must be uploaded to SourceForge.net under Simple PHP Blog or
  // emailed to apalmo <at> bigevilbrain <dot> com

   require_once("config.php");
   require_once("sb_communicate.php");

  // -------------------------
  // Session & Login Functions
  // -------------------------
  function logged_in ( $redirect_to_login, $redirect_to_setup ) {
        
    // Turn off URL SIDs.
    ini_set('url_rewriter.tags','');
    ini_set('session.use_trans_sid', false);
    // the session was expiring before the cookie, this way they are in sync
    ini_set('session.session.gc_maxlifetime', 60*60*24*5);

    // Init the session.
    session_set_cookie_params(60*60*24*5);

    // Check if the user has a client-side cookie.
    if ( isset( $_COOKIE[ 'sid' ] ) ) {
      session_id($_COOKIE[ 'sid' ]);
    }

    // Start the session.
    session_start ();

    // Check if user is logged in.
    if ( isset( $_SESSION[ 'logged_in' ] ) && $_SESSION[ 'logged_in' ] == 'yes' ) {
      if ( $_SESSION[ 'site_path' ] === BASEURL ) {
        if ( $_SESSION[ 'ip' ] === getIP() ) {
          // User is logged in.
          return ( true );
        }
      }
    }
    
    // Special Case:
    // If there's no password file then need to redirect them.
    $username = null;
    $password = null;
    
	@include( CONFIG_DIR . 'password.php' );
    
    if ( $username == null || $password == null ) {
    
      // Clear variables (why not...)
      $username = null;
      $password = null;
      
      if ( $redirect_to_setup ) {
        redirect_to_url( 'install00.php' );
        exit;
      } else {
        return ( false );
      }
    }
    
    // Clear variables (why not...)
    $username = null;
    $password = null;
    
    if ( $redirect_to_login ) {
      redirect_to_url( 'index.php' );
      exit;
    } else {
      return ( false );
    }
  }
  
  function logout () {
    // Turn off URL SIDs.
    ini_set('url_rewriter.tags','');
    ini_set('session.use_trans_sid', false);
    
    // Init the session.
    session_set_cookie_params(60*60*24*5);
    
    // Check if the user has a client-side cookie.
    if ( isset( $_COOKIE[ 'sid' ] ) ) {
      session_id($_COOKIE[ 'sid' ]);
    }
    
    // Start the session.
    session_start();
    
    // Check if user is logged in (just for reporting...)
    $was_logged_in = 0;
    if ( isset( $_SESSION[ 'logged_in' ] ) && $_SESSION[ 'logged_in' ] == 'yes' ) {
      if ( $_SESSION[ 'site_path' ] === BASEURL ) {
        if ( $_SESSION[ 'ip' ] === getIP() ) {
          $was_logged_in = 1;
        }
      }
    }
        
    session_unset();
    session_destroy();
    $_SESSION = array();
    
    return ( $was_logged_in );
  }
  
  function check_password ( $user, $pass ) {
    // Error codes:
    // 100 - Bad login or password (primary or secondary)
    // 101 - Inactive account (secondary only)

    // Check password against hashed password file
    $username = null;
    $password = null;
		
		@include( CONFIG_DIR . 'password.php' );

    if ( $username == null || $password == null ) {
      // Missing password.php file...
    } else {
      // Verify Username
      if ( crypt( $user, $username ) === $username ) {
        if ( crypt( $pass, $password ) === $password ) {
          // Start Session and Set Cookie
          session_unset();
          session_destroy();
          $_SESSION = array();

          ini_set('url_rewriter.tags','');
          ini_set('session.use_trans_sid', false);
          // the session was expiring before the cookie, this way they are in sync
          ini_set('session.session.gc_maxlifetime', 60*60*24*5);

          session_set_cookie_params(60*60*24*5);
          @session_start();

          // Support for PHP >= 4.1.0
          $_SESSION[ 'logged_in' ] = 'yes';
          $_SESSION[ 'site_path' ] = BASEURL;
          $_SESSION[ 'fulladmin' ] = 'yes';
          $_SESSION[ 'ip' ] = getIP();
          $_SESSION[ 'user' ] = 'Administrator';
          $_SESSION[ 'username' ] = 'admin';
          setcookie('sid',session_id(),time()+60*60*24*5);
          
          // Clear variables (why not...)
          $username = null;
          $password = null;

          return ( true );
        }
      }
    }

    // Clear variables (why not...)
    $username = null;
    $password = null;

    // OK, now check for a secondary login (non-admin)
    $secondary = check_secondary_password($user, $pass);

    // Handle error codes
    if ( $secondary > 99 ) {
      return ( $secondary );
    }

    if ( $secondary == true ) {
      return ( true );
    }

    return ( 100 );
  }
  
  function redirect_to_url( $relative_url = "index.php" ) {
		$baseurl = baseurl();
		header('Location: ' . $baseurl . $relative_url);
		exit;
	}
	
	function baseurl() {
    // Many thanks to Ridgarou for fixing the port issue.
  
    if ( strpos ($_SERVER['HTTP_HOST' ], ":") != false ){
      $port = '';
    } else {
      $port = ':' . $_SERVER[ 'SERVER_PORT'];
      if ($port == ':80') {
        $port = '';
      }
      if ($port == ':8080') {
        $port = '';
      }
    }
    
    $proto = 'http://';
    if ($_SERVER['HTTPS'] == 'on') {
      $proto = 'https://';
      if ($port == ':443') {
        $port = '';
      }
    }

    return($proto.$_SERVER[ 'HTTP_HOST' ].$port . BASEURL);
  }
  
  // The rest of this is used for the non-Admin users
  // Users that may only create blog entries and moderate

  function encrypt_password ( $pass ) {
    // Generate and store password hash and log the user in.
    //
    $mypasswd = $pass;
    $hashed = crypt($mypasswd);
    return ( $mypasswd );
  }
  
  function check_secondary_password ( $user, $pass ) {
    // Check password against hashed password file
    //

    $user_list = read_users();

    foreach ($user_list as $tmp) {
      if ( $tmp[1] == $user ) {
        // Is this user active?
        if ( $tmp[4] == 'N' ) {
          return ( 101 );
        }

        // OK, we've found the right secondary user - check their password
        $chkpass = crypt($user, $pass);
        if ($tmp[2] === $chkpass) {
          session_unset();
          session_destroy();
          $_SESSION = array();
          
          ini_set('url_rewriter.tags','');
          ini_set('session.use_trans_sid', false);
          // the session was expiring before the cookie, this way they are in sync
          ini_set('session.session.gc_maxlifetime', 60*60*24*5);

          session_set_cookie_params(60*60*24*7);
          @session_start();

          // Support for PHP >= 4.1.0
          $_SESSION[ 'logged_in' ] = 'yes';
          $_SESSION[ 'site_path' ] = BASEURL;
          $_SESSION[ 'fulladmin' ] = 'no';
          $_SESSION[ 'ip' ] = getIP();
          $_SESSION[ 'user' ] = $tmp[0];
          $_SESSION[ 'username' ] = $tmp[1];
          setcookie('sid',session_id(),time()+60*60*24*7);
          return ( true );
        } else {
          return ( false );
        }
      }
    }
  }
?>
