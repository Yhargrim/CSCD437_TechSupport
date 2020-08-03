#define _GNU_SOURCE
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdbool.h>
#include <regex.h>
#include <limits.h>
#include <errno.h>

struct Logs {
	char* first;
	char* second;
	char* third;
	char* fourth;
};

void logging(struct Logs*, char*);
static void name(char*);
void cullNewLine(char*);
static void number(char*);
static void openFile(char*);
static void password();
static void reenterPassword();
static void writeToOutput();
void numError(char*, char*);

static struct Logs mErrors; 
static bool mNameCorrect, mNumberCorrect, mInFileCorrect, mOutFileCorrect, mPasswordCorrect;
static FILE* mInput, mOutput;
static char* mFirst;
static char* mLast;
static char* mNum1;
static char* mNum2;
static char* mPassword;

void main() {
	name("first");
	name("last");
	printf("Name: %s %s\n", mLast, mFirst);

	number("first");
	number("second");
	/*
	openFile("r");
	char ch;
	while((ch = fgetc(mInput)) != EOF) {
		printf("%c", ch);
	}

	openFile("w");
	fprintf(mOutput, "Testing output file.\n");

	password();
	printf("Password: %s\n", mPassword);
	*/
	printf("Error:\n\t%s\n\t%s\n\t%s\n\t%s\n", mErrors.first, mErrors.second, mErrors.third, mErrors.fourth);

}

void logging(struct Logs* mLog, char* log) {
	if(!mLog->first) {
		mLog->first = malloc(128);
		strcpy(mLog->first, log);
	} else if(!mLog->second) {
		mLog->second = malloc(128);
		strcpy(mLog->second, log);
	} else if(!mLog->third) {
		mLog->third = malloc(128);
		strcpy(mLog->third, log);
	} else if(!mLog->fourth) {
		mLog->fourth = malloc(128);
		strcpy(mLog->fourth, log);
	} else
		printf("Error logs full.\n");
}

bool regexMatch(char* str, char* pattern) {
	regex_t regex;
	int regInt;
	regInt = regcomp(&regex, pattern, REG_EXTENDED);
	if(regInt != 0)
		return false;
	regInt = regexec(&regex, str, 0, NULL, 0);
	regfree(&regex);
	if(regInt == 0)
		return true;
	return false;
}

static void name(char* which) {
	printf("Enter %s name: ", which);
	char* name = malloc(51);
	char* log = malloc(128);
 	if(fgets(name, 51, stdin)) {
		cullNewLine(name);
		char* pattern = "^[A-Za-z,.'-]{1,50}$";
		if(regexMatch(name, pattern)) {
			mNameCorrect = true;
			if(strcmp(which, "first") == 0) {
				mFirst = name;
		 	} else {
				mLast = name;
			}
		} else {
			asprintf(&log, "Error when reading %s name. Entered bad value '%s'\n", which, name);
			cullNewLine(log);
			logging(&mErrors, log);
		}
	} else {
		asprintf(&log, "Error when reading %s name. Entered bad value '%s'\n", which, name);
		cullNewLine(log);
		logging(&mErrors, log);
	}
}

void cullNewLine(char* str) {
	char* noline = NULL;
	noline = strchr(str, '\n');
	if(noline) {
		*noline = '\0';
	} else {
		while(getchar() != '\n');
	}
}

static void number(char* which) {
	printf("Enter %s integer: ", which);
	char* number = malloc(48);
	if(fgets(number, 48, stdin)) {
		cullNewLine(number);
		char* pattern = "^(-?[0-9]+){1,11}$";
		if(regexMatch(number, pattern)) {
			char* endptr;
			long num = strtol(number, &endptr, 10);
			if(endptr != number) {
				if(errno == 0) {
					if(num >= INT_MIN && num <= INT_MAX) {
						mNumberCorrect = true;
						if(strcmp(which, "first") == 0) {
							mNum1 = number;
						} else {
							mNum2 = number;
						}
					} else {
						numError(which, number);
					}
				} else {
					numError(which, number);
				}
			} else {
				numError(which, number);
			}
		} else {
			numError(which, number);
		}
	} else {
		numError(which, number);
	}
}

void numError(char* which, char* number) {
	char* log = malloc(128);
	asprintf(&log, "Error when reading %s number. Entered bad value '%s'\n", which, number);
	cullNewLine(log);
	logging(&mErrors, log);
}
/*
FILE* openFile(char* mode) {
	printf("Enter input file name: ");
	char* fname = malloc(128);
	FILE* file;
	bool flag = true;
	while (flag) {
		if (fgets(fname, 128, stdin)) {
			cullNewLine(fname);
			file = fopen(fname, mode);
			if (file) {
				flag = false;
			}
		}
		if (flag) {
			printf("Invalid - Try Again: ");
		}
	}
	return file;
}

char* password() {
	char* first = malloc(128);
	char* second = malloc(128);
	bool flag = true;
	while (flag) {
		printf("Enter desired password: ");
		if (fgets(first, 128, stdin)) {
			cullNewLine(first);
			printf("Re-enter password: ");
			if (fgets(second, 128, stdin)) {
				cullNewLine(second);
				if (strcmp(first, second) == 0) {
					flag = false;
				}
			}
		}
		if (flag) {
			printf("Invalid - Try Again\n");
		}
	}
	free(second);
	return first;
}
*/
