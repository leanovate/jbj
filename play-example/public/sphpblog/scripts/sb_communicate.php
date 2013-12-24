<?php 

	// The Simple PHP Blog is released under the GNU Public License.
	//
	// You are free to use and modify the Simple PHP Blog. All changes 
	// must be uploaded to SourceForge.net under Simple PHP Blog or
	// emailed to apalmo <at> bigevilbrain <dot> com
	
	// getIP()
	// sb_mail ($from, $to, $subject, $body, $text=true, $priority=3)
	// sb_ping ( $url )
	// sb_tb_ping ( $url, $title, $permalink, $excerpt )

	// --------------------
	// RSS & Ping Functions
	// --------------------
	
	// This is so I can start to track IPs
	function getIP() {
		return $_SERVER[ 'REMOTE_ADDR' ];
	}
	
	function sb_mail ($from, $to, $subject, $body, $text=true, $priority=3) {
		// Contributed by: Javier Gutierrez, guti <at> ya <dot> com
		//
		// 1/13/06
		// Added functionality to send mass *individual* emails by
		// passing $to as an array. (Or, use a comma delimited string...)
		global $sb_info;

		$from = str_replace(array("\r", "\n"), "", $from);
	
		$to_array = array();
		if ( is_array( $to ) ) {
			$to_array = $to;
		} else {
			array_push( $to_array, $to );
		}
	
		$result = false;
		
		$headers='';
		if ($text) {
			$headers .= "Content-Type: text/plain \r\n";
		} else {
			$headers .= "MIME-Version: 1.0 \r\n";
			$headers .= "Content-type: text/html; charset=iso-8859-1 \r\n";
		}
		$headers .= 'From: ' . $from . " \r\n";
		$headers .= 'Reply-To: ' . $from . " \r\n";
		$headers .= 'Return-Path: ' . $from . " \r\n";
		$headers .= 'Date: ' . date("r") . " \r\n";
		$headers .= 'X-Priority: ' . strval(intval($priority)) . " \r\n";
		$headers .= 'X-Mailer: SPHPBLOG/' . $sb_info[ 'version' ] . " \r\n";
		
		ini_set('sendmail_from', $from);
		for ( $j=0; $j < count($to_array); $j++ ) {
			$result = mail( $to_array[$j], sb_stripslashes($subject), sb_stripslashes($body), $headers );
		}
		ini_restore('sendmail_from');
		
		return( $result );
	}
	
	// Send a update ping to a ping server in url.
	//
	function sb_ping ( $url ) {
		// Contributed by: Javier Gutierrez, guti <at> ya <dot> com
		//
		if ( $url == NULL || $url == '' ) {
			return( false );
		}
		
		global $blog_config, $sb_info;

		$result = '';
		$user_agent = 'SPHPBLOG ping script/' . $sb_info[ 'version' ];
		$url = parse_url($url);

		$xml="<?xml version=\"1.0\"?".">\n";
		$xml .= "<methodCall>\n";
		$xml .= "\t<methodName>weblogUpdates.ping</methodName>\n";
		$xml .= "\t<params>\n";
		$xml .= "\t\t<param><value>" . htmlspecialchars( strip_tags( $blog_config->getTag('BLOG_TITLE') ) ) . "</value></param>\n";
		// Use external name (SERVER_NAME) for pings to the outside world!!
		if ( ( dirname($_SERVER[ 'PHP_SELF' ]) == '\\' || dirname($_SERVER[ 'PHP_SELF' ]) == '/' ) ) {
		   $xml=$xml . "\t\t<param><value>http://" . $_SERVER[ 'SERVER_NAME' ] . "/</value></param>\n";
		} else {
		   $xml=$xml . "\t\t<param><value>http://" . $_SERVER[ 'SERVER_NAME' ] . dirname( $_SERVER[ 'PHP_SELF' ] ) . "/</value></param>\n";
		}
		$xml .= "\t</params>\n";
		$xml .= "</methodCall>\n";
		$socket = @fsockopen( $url[ 'host' ], 80, $errno, $errstr, 30);
		if ( $socket ) { 
			fwrite( $socket, 'POST ' . $url[ 'scheme' ] . '://' . $url[ 'host' ] . $url[ 'path' ] . " HTTP/1.0\nHost: " . $url[ 'host' ] . "\nUser-Agent:" . $user_agent . " 0.1\nContent-Type: text/xml\nContent-Length: " . strlen ( $xml ) . "\n\n" . $xml . "\n" );
			$result = fread( $socket, 8192 );
			fclose ( $socket );
			return( strpos( $result, 'fault' )===false );
		}
		else {
			return( false );
		}
	}
	
	// Send a trackback ping to a ping server in url.
	//
	function sb_tb_ping ( $url, $title, $permalink, $excerpt ) {
		// Contributed by: Arjan Hakkesteegt, sphpblog <at> hakwerk <dot> com
		//
		if ( $url == NULL || $url == '' || $permalink == NULL || $permalink == '' ) {
			return( false );
		}
		
		global $blog_config, $sb_info;

		$result = '';
		$user_agent = 'SPHPBLOG ping script/' . $sb_info[ 'version' ];
		$url = parse_url($url);
		
		$data = '';
		$data  .= 'title=' . urlencode( strip_tags( $title ) );
		// Use external name (SERVER_NAME) for pings to the outside world!!
		if ( ( dirname($_SERVER[ 'PHP_SELF' ]) == '\\' || dirname($_SERVER[ 'PHP_SELF' ]) == '/' ) ) {
		   $data = $data . '&url=http://' . $_SERVER[ 'SERVER_NAME' ] . '/' . urlencode( strip_tags( $permalink ) );
		} else {
		   $data = $data . '&url=http://' . $_SERVER[ 'SERVER_NAME' ] . dirname( $_SERVER[ 'PHP_SELF' ] ) . '/' . urlencode( strip_tags( $permalink ) );
		}
		$data  .= '&excerpt=' . urlencode( strip_tags( $excerpt ) );
		$data  .= '&blog_name=' . urlencode( strip_tags( $blog_config->getTag('BLOG_TITLE') ) );
		
		// $socket = fsockopen( $url[ 'host' ], 80, $errno, $errstr, 30);
		$socket = fsockopen( ( $url[ 'host' ] === $_SERVER[ 'HTTP_HOST' ] ? $_SERVER[ 'SERVER_ADDR' ] : $url[ 'host' ] ), 80, $errno, $errstr, 30);
		if ( $socket ) { 
			fwrite( $socket, 'POST ' . $url[ 'scheme' ] . '://' . $url[ 'host' ] . $url[ 'path' ] . '?' . $url[ 'query' ] . " HTTP/1.0\nHost: " . $url[ 'host' ] . "\nUser-Agent:" . $user_agent . " 0.1\nContent-Type: application/x-www-form-urlencoded\nContent-Length: " . strlen ( $data ) . "\n\n" . $data . "\n" );
			$result = fread( $socket, 8192 );
			fclose ( $socket );
			return( strpos( $result, '<error>0</error>' ) );
		}
		else {
			return( false );
		}
	}
	
?>
