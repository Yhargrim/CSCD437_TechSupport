#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdbool.h>

char* name(char*);
void cullNewLine(char*);
int* numbers();
int number();
FILE* openFile(char*);
char* password();

int main() {
	char* first = name("first");
	char* last = name("last");
	printf("Name: %s %s\n", first, last);

	int* nums = numbers();
	printf("Addition: %d\n", nums[0] + nums[1]);
	printf("Multiply: %d\n", nums[0] * nums[1]);

	FILE* infile = openFile("r");
	char ch;
	while((ch = fgetc(infile)) != EOF) {
		printf("%c", ch);
	}

	FILE* outfile = openFile("w");
	fprintf(outfile, "Testing output file.\n");

	char* pswrd = password();
	printf("Password: %s\n", pswrd);

	free(first);
	free(last);
	fclose(infile);
	fclose(outfile);
	free(pswrd);
	return 0;
}

char* name(char* which) {
	printf("Enter your %s name: ", which);
	char* hold = malloc(51);
	bool flag = true;
	while (flag) {
 		if(fgets(hold, 51, stdin) != NULL) {
			flag = false;
		} else {
			printf("Invalid - Try Again: ");
		}
	}
	cullNewLine(hold);
	return hold;
}

void cullNewLine(char* str) {
	char* noline = NULL;
	noline = strchr(str, '\n');
	if(noline) {
		*noline = '\0';
	}
}

int* numbers() {
	printf("Enter an integer: ");
	int one = number();

	printf("Enter another integer: ");
	int two = number();

	static int arr[2];
	arr[0] = one;
	arr[1] = two;
	return arr;
}

int number() {
	int hold;
	bool flag = true;
	while (flag) {
		if (scanf("%d", &hold) == 1) {
			flag = false;
		} else {
			printf("Invalid - Try Again: ");
		}
		while((getchar()) != '\n');
	}
	return hold;
}

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
