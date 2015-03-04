﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.Net;
using System.Net.Sockets;
using System.IO;

namespace IPExport
{
    /// <summary>
    ///  This class is responsible for communicating with the remote server and perform operations such as uploading
    ///  video files and posting requests with upload meta-data.
    /// </summary>
    class Uploader
    {
        public bool upload(string username, string password)
        {
            Process upload = null;
            string videoDir = "\"" + ExportUtils.getExecutableDir() + ExportConstants.VIDEO_DIR + ExportConstants.VIDEO_PATTERN + "\"";

            string args = "-pw " + password + " " + videoDir + " " + ExportConstants.UPLOAD_TARGET;

            Console.WriteLine("Upload arguments: " + args);

            try
            {
                upload = Process.Start(ExportConstants.SCP_EXE, args);
                upload.WaitForExit();

                return true;
            }
            catch (Exception ex)
            {
                if (upload != null)
                {
                    upload.Close();
                }

                Console.WriteLine("Exception while uploading, message: " + ex.Message + ", data: " + ex.Data + ", trace: " + ex.StackTrace);
                throw ex;
            }
        }

        public string sendSvx(byte[] bytes, string username, string password)
        {
            string authHeaderValue = "Basic " + base64Encode(username + ":" + password);

            try
            {
                WebRequest request = WebRequest.Create(ExportConstants.UPLOAD_URL);
                request.ContentType = ExportConstants.UPLOAD_CONTENT_TYPE;
                request.Method = ExportConstants.UPLOAD_METHOD;
                request.ContentLength = bytes.Length;
                request.Headers.Add("Authorization", authHeaderValue); // Requires user to be present on server

                Stream stream = request.GetRequestStream();
                stream.Write(bytes, 0, bytes.Length);
                stream.Close();
                HttpWebResponse response = (HttpWebResponse)request.GetResponse();

                Console.WriteLine("Sent SXV, status: " + response.StatusDescription + ", code: " + response.StatusCode);

                return response.StatusDescription;
            }
            catch (WebException ex)
            {
                Console.WriteLine("Exception while uploading, status: " + ex.Status + ", response: " + ex.Response + ", trace: " + ex.StackTrace);
                throw ex;
            }
        }

        public bool serverIsAvailable()
        {
            return isAvailable(ExportConstants.SERVER_BASE_URL);
        }

        private bool isAvailable(string url)
        {
            try
            {
                TcpClient client = new TcpClient(url, 80);
                client.Close();
                return true;
            }
            catch (Exception)
            {
                return false;
            }
        }

        private string base64Encode(string value)
        {
            byte[] bytes = Encoding.UTF8.GetBytes(value);
            return Convert.ToBase64String(bytes);
        }
    }
}
