#define _GNU_SOURCE
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdbool.h>
#include <unistd.h>
#include <regex.h>
#include <limits.h>
#include <errno.h>
#include <openssl/sha.h>
#include "sodium.h"

struct Logs {
    char* first;
    char* second;
    char* third;
    char* fourth;
};

void logging(struct Logs*, char*);
void name(char*);
bool regexMatch(char*, char*);
void cullNewLine(char*);
void number(char*);
void openFile(char*);
void password();
void hashPassword(char*);
char* hash(char*, char*);
void reenterPassword();
void writeToOutput();
void passwordError(char*);
void writeError();
void writeToFile(char*, char*);
void numError(char*, char*);
void fileError(char*, char*);
void writeToOutput();

static struct Logs mErrors;
static bool mFirstNameCorrect, mLastNameCorrect, mNum1Correct, mNum2Correct, mInFileCorrect, mOutFileCorrect, mPasswordCorrect;
static FILE* mInput;
static FILE* mOutput;
static char* mFirst;
static char* mLast;
static char* mNum1;
static char* mNum2;
static char* mPassword;
static char* mPasswordFile;

int main(int argc, char *argv[]) {
    (void)argc;
    (void)argv;
    name("first");
    name("last");

    number("first");
    number("second");

    openFile("input");
    openFile("output");

    password();
    reenterPassword();

    writeToOutput();
    writeToFile("errors-c.txt", NULL);
    printf("Error:\n\t%s\n\t%s\n\t%s\n\t%s\n", mErrors.first, mErrors.second, mErrors.third, mErrors.fourth);
    return 0;
}

void name(char* which) {
    printf("Enter %s name: ", which);
    char* name = malloc(51);
    char* log = malloc(128);
    if(fgets(name, 51, stdin)) {
        cullNewLine(name);
        char* pattern = "^[A-Za-z,.'-]{1,50}$";
        if(regexMatch(name, pattern)) {
            if(strcmp(which, "first") == 0) {
                mFirst = name;
                mFirstNameCorrect=true;
            }
            else {
                mLast = name;
                mLastNameCorrect=true;
            }
        } else {
            asprintf(&log, "Error when reading %s name. Entered bad value '%s'.\n", which, name);
            cullNewLine(log);
            logging(&mErrors, log);
        }
    } else {
        asprintf(&log, "Error when reading %s name. Entered bad value '%s'.\n", which, name);
        cullNewLine(log);
        logging(&mErrors, log);
    }
}

void cullNewLine(char* str) {
    char* noline = NULL;
    noline = strchr(str, '\n');
    if(noline) *noline = '\0';
    else while(getchar() != '\n');
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
    } else printf("Error logs full.\n");
}

bool regexMatch(char* str, char* pattern) {
    regex_t regex;
    int regInt;
    regInt = regcomp(&regex, pattern, REG_EXTENDED);
    if(regInt != 0) return false;
    regInt = regexec(&regex, str, 0, NULL, 0);
    regfree(&regex);
    if(regInt == 0) return true;
    return false;
}

void number(char* which) {
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
                        if(strcmp(which, "first") == 0) {
                            mNum1 = number;
                            mNum1Correct=true;
                        }
                        else {
                            mNum2 = number;
                            mNum2Correct;
                        }
                    } else numError(which, number);
                } else numError(which, number);
            } else numError(which, number);
        } else numError(which, number);
    } else numError(which, number);
}

void numError(char* which, char* number) {
    char* log = malloc(128);
    asprintf(&log, "Error when reading %s number. Entered bad value '%s'.\n", which, number);
    cullNewLine(log);
    logging(&mErrors, log);
}

void openFile(char* mode) {
printf("Enter %s file name: ", mode);
    char* fname = malloc(256);
    char* rorw;
    if(fgets(fname, 256, stdin)) {
        cullNewLine(fname);
        char* pattern = "^[0-9A-Za-z,.'-]+{1,200}\\.([A-Za-z]+){1,20}$";
        if(regexMatch(fname, pattern)) {
            if(strcmp(mode, "input")==0) rorw = "r";
            else rorw = "w";
            FILE* file = fopen(fname, rorw);
            if(file) {
                if(strcmp(rorw, "r")==0) {
                    mInput = file;
                    mInFileCorrect = true;
                } else {
                    mOutput = file;
                    mOutFileCorrect = true;
                }
            } else fileError(mode, fname);
        } else fileError(mode, fname);
    } else fileError(mode, fname);
}


void fileError(char* mode, char* fname) {
    char* log = malloc(356);
    asprint(&log, "Error when reading %s file. Entered bad value '%s'.\n", mode, fname);
    cullNewLine(log);
    logging(&mErrors, log);
}

void password() {
	printf("Enter password: ");
	char* password=(char*)malloc(129*sizeof(char));
    char* log = malloc(128);
	if(fgets(password, 129*sizeof(char), stdin)) {
		cullNewLine(password);
        char* pattern = "^((?!\\0).){1,128}$";
        if(regexMatch(password, pattern)) {
            mPasswordCorrect=true;
            hashPassword(password);
        }
        else {
            asprintf(&log, "Error when reading password. Entered bad value '%s'.\n", password);
            cullNewLine(log);
            logging(&mErrors, log);
        }
	}
    else {
        asprintf(&log, "Error when reading password. Entered bad value '%s'.\n", password);
        cullNewLine(log);
        logging(&mErrors, log);
    }
}

void hashPassword(char* password) {
    char salt[32];
    randombytes_buf(salt, 32);
    char* h=salt;
    h=hash(h, password);

    writeToFile("password.txt", hash);
}

char* hash(char* hash, char*password) {
    char data[] = password;
    char h[SHA512_DIGEST_LENGTH];
    SHA512(data, sizeof(data) - 1, h);
    hash=hash+(char)h;

    return hash;
}

void reenterPassword() {
    if(mPasswordCorrect) {
        printf("Reenter password: ");
	    char* password=(char*)malloc(129*sizeof(char));
        char* log = malloc(128);
	    if(fgets(password, 129*sizeof(char), stdin)) {
            cullNewLine(password);
            FILE* file=fopen(mPasswordFile, "w+");
            char firstPass[64];
            fscanf(file, "%[^\n]", firstPass);
            char* salt=strtok(firstPass, "\\]")+(char)"\\]";
            char* h=hash(salt, password);
            bool same=true;
            int cur=0;
            int i=strnlen(h, 65);
            while(cur<i) {
                same=firstPass[i]==h[i];
                cur++;
                if(!same) break;
            }
            if(same) printf("Passwords are equivalent.");
            else passwordError(password);
        }
        else passwordError(password);
    } else printf("Previous password not formatted correctly, can't re-enter password.\n");
}

void writeToFile(char* fileName, char* hash) {
    FILE* file = NULL;
    file=fopen(fileName, "w+");
    char* fname=fileName;
    while(access( file, F_OK ) != -1 ) {
        fname=0+fname;
        file=fopen(fname, "w+");
    }
    if(fileName=="password.txt") mPasswordFile=fname;

    if(fileName=="errors-c.txt") writeError();
    else fputs(hash, file);
    close(file);
}

static void writeError() {
    FILE* fout;
    fout = fopen("errors-c.txt", "w+");
    fprintf(fout, "%s", mErrors.first);
    fprintf(fout, "%s", mErrors.second);
    fprintf(fout, "%s", mErrors.third);
    fprintf(fout, "%s", mErrors.fourth);
    fclose(fout);
}


void passwordError(char* password) {
    char* log = malloc(128);
    asprintf(&log, "The password '%s' does not match the first password entered.\n", password);
    cullNewLine(log);
    logging(&mErrors, log);
}

void writeToOutput() {
    char* log = malloc(128);
    if(mOutFileCorrect) {
        FILE* file=fopen(mOutput, "w+");
        if(mFirstNameCorrect) {
            fputs(mFirst+' ', file);
            if(!mLastNameCorrect) fputs('\n', file);
        }
        if(mLastNameCorrect) fputs(mLast+'\n', file);
        if(!(mFirstNameCorrect&&mLastNameCorrect)) {
            asprintf(&log, "Could not write name to output file; valid names not given.\n");
            cullNewLine(log);
            logging(&mErrors, log);
        }

        if(mNum1Correct&&mNum2Correct) {
            char* endptr;
            long num1 = strtol(mNum1, &endptr, 10);
            long num2 = strtol(mNum2, &endptr, 10);
            long add=num1+num2;
            long mult=num1*num2;
            fputs(num1+'+'+num2+'='+add+'\n', file);
            fputs(num1+'*'+num2+'='+mult+'\n', file);
        } else {
            asprintf(&log, "Could not write integer addition/multiplication to output file; one or both integers not valid.\n");
            cullNewLine(log);
            logging(&mErrors, log);
            fputs('\n', file);
        }

        if(mInFileCorrect) {
            char* s="Input file contents:\n";
            fputs(s, file);
            FILE* input=fopen(mInput, "w+");

            char* line = NULL;
            size_t len = 0;
            ssize_t read;

            while ((read = getline(&line, &len, input)) != -1) fputs(line, file);
        } else {
            asprintf(&log, "Could not write input file contents to output file; valid filename not given.\n");
            cullNewLine(log);
            logging(&mErrors, log);
        }
    } else {
        asprintf(&log, "Could not write to output file; valid filename not given.\n");
        cullNewLine(log);
        logging(&mErrors, log);
    }


}