#!/usr/bin/python
import requests
from requests.auth import HTTPDigestAuth
import sys, getopt

def main(argv):
	inputfile, outputfile, url, verify_certificate, user, password = get_options(argv)
	filehandle = open(inputfile)
	request = requests.post(url, auth=HTTPDigestAuth(user, password), files = {'upload':filehandle}, verify=verify_certificate)
	if request.status_code == requests.codes.ok:
		with open(outputfile, 'wb') as handle:
			for block in request.iter_content(1024):
				if not block:
					break
				handle.write(block)		
	else:
		print "Something went wrong."
		print request.text
		print request.raise_for_status()

def get_options(argv):
	inputfile = ''
	outputfile = ''
	url = ''
	verify_certificate = True
	user = ''
	password = ''
	try:
		opts, args = getopt.getopt(argv,"hci:o:u:b:p:",["ifile=","ofile=","url=","user=","password="])
	except getopt.GetoptError:
		print 'uploadAndReceive.py -h to show help'
		sys.exit(2)
	for opt, arg in opts:
		if opt == '-h':
			print 'uploadAndReceive.py -i <inputfile> -o <outputfile> -u <url> -us <user> -pw <password>'
			print '-c to not verify the https certifcate'
			sys.exit()
		elif opt in ("-i", "--ifile"):
			inputfile = arg
		elif opt in ("-o", "--ofile"):
			outputfile = arg
		elif opt in ("-u", "--url"):
			url = arg
		elif opt in ("-c"):
			verify_certificate = False
		elif opt in ("-b"):
			user = arg
		elif opt in ("-p"):
			password = arg
	return inputfile, outputfile, url, verify_certificate, user, password

if __name__ == "__main__":
   main(sys.argv[1:])
