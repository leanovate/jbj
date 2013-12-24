<?php
	/**
	* File IO and file system related functions.
	*
	* @author		Alexander Palmo <apalmo at bigevilbrain dot com>
	* @access		public static
	*
	* read_file( $filename )
	* write_file( $filename, $str )
	* delete_file( $filename )
	* make_dir( $dir, $mode=0777 )
	* copy_dir( $source, $dest )
	* remove_dir( $dir )
	* file_listing( $dir, $ext_array=array() )
	* folder_listing( $dir )
	* strip_extension( $filename )
	*/

	// TODO remove config.php dependency (BLOG_MASK)	
	class fileio {
		
		/**
		* Read file. Transparently gunzips file based on file extension.
		*
		* Example Usage:
		* $str = fileio::read_file( "folder/something.txt" );
		* $str = fileio::read_file( "folder/something.txt.gz" );
		*	
		* @param		string $filename
		* @return		string contents of file or NULL on error.
		*/
		function read_file($filename, $use_cache=false) {
			global $filecache, $filecount, $filecachecount;
			
			if (!isset($filecache)) {
				$filecache = array();
			}
			
			if (array_key_exists($filename, $filecache)) {
				if ($use_cache) {
					if ($filecache[$filename]['lastmodified'] == @filemtime($filename)) {					
						if (!isset($filecachecount)) {
							$filecachecount = 1;
						} else {
							$filecachecount++;
						}
						return $filecache[$filename]['content'];
					}
				}
			}
			
			if ( file_exists($filename) ) {
					$str = file_get_contents( $filename );
//print "reading from: ". substr($filename, 0, strrpos($filename, '.'));
					// this is to transition off of .gz stored files, TODO it can be removed in a long time.
					if ( strtolower( strrchr( $filename, '.' ) ) == '.gz' && extension_loaded( 'zlib' ) ) {
						$str = gzinflate( substr( $str, 10 ) );
						// write out txt here, delete .gz
						if ($this->write_file(substr($filename, 0, strrpos($filename, '.')) . ".txt", $str) !== FALSE) {
						  $this->delete_file($filename);
                                                }
					}
					
					// Cache file data...
					$cache = array();
					$cache['lastmodified'] = @filemtime($filename);
					$cache['content'] = $str;
					$filecache[$filename] = $cache;
					
					if (!isset($filecount)) {
						$filecount = 1;
					} else {
						$filecount++;
					}
					return $str;
			}
		}
		
		/**
		* Write file. Transparently gzips file based on file extension.
		*
		* Example Usage:
		* $bytes = fileio::write_file( "folder/something.txt", "foobar" );
		* $bytes = fileio::write_file( "folder/something.txt.gz", "foobar" );
		*
		* @param		string $filename
		* @param		string $str
		* @return		integer/false (bytes written or FALSE on error)
		*/
		function write_file( $filename, $str ) {
			
			//if ( strtolower( strrchr( $filename, '.' ) ) == '.gz' && extension_loaded( 'zlib' ) ) {
			//	$str = gzencode( $str, 9 );
			//}
			fileio::make_dir(dirname($filename));
			
			@umask(0);
			
			$length = strlen($str);
			$bytes_written = @file_put_contents( $filename, $str );
			
			if ( $length == $bytes_written ) {
				@chmod($filename, BLOG_MASK);
				return $bytes_written;
			} else {
				return false;
			}
		}
		
		/**
		* Delete file.
		*
		* Example Usage:
		* $success = fileio::delete_file( "folder/something.txt" );
		*
		* @param		string $filename
		* @return		boolean
		*/
		function delete_file( $filename ) {
			
			if ( file_exists($filename) ) {
				$result = @unlink( $filename );
				clearstatcache();
				return $result;
			}
			return false;
		}
		
		/**
		* Recursive create directory.
		*
		* Example Usage:
		* $success = fileio::make_dir( "folder/foo/" ); // Creates 'folder' and 'folder/foo'
		*
		* @param		string $dir
		* @return		boolean
		*/
		function make_dir( $dir, $mode=BLOG_MASK ) {
			
			@umask(0);
			if (is_dir($dir) || @mkdir($dir,$mode)) {
				return true;
			}
			if (!fileio::make_dir(dirname($dir),$mode)) {
				return false;
			}
			if (@mkdir($dir,$mode)) {
				return true;
			} else {
				return false;
			}
		}
		
		/**
		* Copy a file, or recursively copy a folder and its contents
		*
		* Example Usage:
		* $files = fileio::copy_dir( "folder1/", "folder2/" );
		*
		* @author		Aidan Lister <aidan@php.net>
		* @version	1.0.1
		* @link			http://aidanlister.com/repos/v/function.copyr.php
		*
		* @param		string $source    Source path
		* @param		string $dest      Destination path
		* @return		boolean
		*/
		function copy_dir( $source, $dest ) {
			// Simple copy for a file
			if (is_file($source)) {
				if ( copy($source, $dest) ) {
					@unlink($source);
					return true;
				} else {
					return false;
				}
			}
		
			// Make destination directory
			if ( !is_dir($dest) ) {
				@umask(0);
				mkdir($dest, BLOG_MASK);
			}
		
			// Loop through the folder
			$dir = opendir($source);
			while ($file = readdir($dir) ) {
				if ($file == '.' || $file == '..') {
					continue;
				}
		
				// Deep copy directories
				if ($dest !== $source.'/'.$file) {
					fileio::copy_dir($source.'/'.$file, $dest.'/'.$file);
				}
			}
		
			// Clean up
			closedir($dir);
			rmdir($source);
			
			clearstatcache();
			
			return true;
		}
	
		/**
		* Recursive delete directory.
		*
		* Example Usage:
		* $success = fileio::remove_dir( "folder/foo" ); // Recursively deletes contents of 'foo' and 'foo' directory.
		* $success = fileio::remove_dir( "folder/foo/" ); // Recursively deletes contents of 'foo' (but not the directory itself.)
		*
		* @param		string $filename
		* @return		null
		*/
		function remove_dir( $dir ) {
			if ($handle = @opendir("$dir")) {
				while (false !== ($item = readdir($handle))) {
					if ($item != "." && $item != "..") {
						if (is_dir("$dir/$item")) {
							fileio::remove_dir("$dir/$item");
						} else {
							@unlink("$dir/$item");
						}
					}
				}
				closedir($handle);
				return @rmdir($dir);
			}
		}
		
		/**
		* Return an array of files in a directory.
		*
		* Example Usage:
		* $files = fileio::file_listing( "folder/", array('.txt','.jpg') );
		*
		* @param		string $dir
		* @param		array $ext_array all lower-case
		* @return		array
		*/
		function file_listing( $dir, $ext_array=array() ) {
			if (substr($dir, -1, 1) != "/") { // Must have trailing slash
				$dir .= "/";
			}

			$result = array();
			if ($handle = @opendir($dir)) {
				while (false !== ($filename = readdir($handle))) {
					if ($filename != "." && $filename != ".." && is_file( $dir . $filename )) {
						if ( count( $ext_array ) > 0 ) { // Extension filter
							if ( in_array( strtolower( strrchr( $filename, '.' ) ), $ext_array ) ) {
								array_push( $result, $filename );
							}
						} else { // No filter
							array_push( $result, $filename );
						}
					}
				}
				closedir( $handle );
			}
			sort( $result );
			return $result;
		}
		
		/**
		* Return an array of files in a directory.
		*
		* Example Usage:
		* $files = fileio::file_listing( "folder/", array('.txt','.jpg') );
		*
		* @param		string $dir
		* @param		array $ext_array all lower-case
		* @return		array
		*/
		function folder_listing( $dir ) {
			if (substr($dir, -1, 1) != "/") { // Must have trailing slash
				$dir .= "/";
			}

			$result = array();
			if ($handle = @opendir($dir)) {
				while (false !== ($filename = readdir($handle))) {
					if ($filename != "." && $filename != ".." && is_dir( $dir . $filename )) {
						array_push( $result, $filename );
					}
				}
				closedir( $handle );
			}
			sort( $result );
			return $result;
		}
		
		/**
		* Return filename without extensions.
		*
		* Example Usage:
		* $filename = fileio::strip_extension( "folder/index.txt.gz" ); // Returns "index"
		*
		* @param		string $filename
		* @return		string
		*/
		function strip_extension( $filename ) {
			$filename = basename($filename);
			$temp_pos = strpos( $filename, '.' );
			if ( $temp_pos !== false ) {
				$filename = substr( $filename, 0, $temp_pos );
			}
			return $filename;
		}
		
	}
	
	
	/**
	* Wrappers for legacy functions. New code should just call the above class directly.
	*/
	function sb_read_file( $filename ) {
		return fileio::read_file($filename);
	}
	
	function sb_write_file( $filename, $str ) {
		return fileio::write_file($filename, $str);
	}
  
	function sb_create_folder( $dir, $mode=BLOG_MASK ) {
		return fileio::make_dir($dir, $mode);
	}
  
	function sb_copy( $source, $dest ) {
		return fileio::copy_dir($source, $dest);
	}
	
	function sb_folder_listing( $dir, $ext_array ) {
		return fileio::file_listing($dir, $ext_array=array());
	}
  
	function sb_delete_file( $filename ) {
		return fileio::delete_file($filename);
	}
  
	function sb_delete_directory( $dir ) {
		return fileio::remove_dir($dir);
	}
  
	function sb_strip_extension( $filename ) {
		return fileio::strip_extension($filename);
	}
	
	
	
	
		
	/**
	* Returns memory usage, file usage and execution time information for debugging
	*
	* @return		string		html
	*/
	function resource_report() {
		$end_time = microtime_float();
		$memory_usage = 'Memory Usage: '.(memory_get_usage(true)/1024).' KB';
		$execution_time = 'Execution Time: '.($end_time-$GLOBALS['start_time']).' seconds';
		$files_accessed = 'Files Accessed: '.count($GLOBALS['filecache']).' unique files / '.$GLOBALS['filecachecount'].' from cache / '.$GLOBALS['filecount'].' from disk';
		
		return("$memory_usage<br />$execution_time<br />$files_accessed");
	}
?>
