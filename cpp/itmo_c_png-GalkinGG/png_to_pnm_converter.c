#include "return_codes.h"

#include <ctype.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef unsigned int uint;
typedef unsigned char uchar;

#if defined(ZLIB)
#include <zlib.h>
#elif defined(LIBDEFLATE)
#include <libdeflate.h>
#elif defined(ISAL)
#include <include/igzip_lib.h>
#else
#error "Unsupported library!"
#endif

typedef struct chunk
{
	int length;
	uchar type[4];
	uint CRC;
	uchar data[];
} chunk;

typedef enum
{
	UNEXPECTED_PLTE = (-1),
	UNNECESSARY_PLTE = 0,
	NECESSARY_PLTE = 1,
} PLTE_codes;

typedef enum
{
	IHDR,
	PLTE,
	IDAT,
	IEND,
} chunk_codes;

typedef enum
{
	SUB = 1,
	UP,
	AVG,
	PAETH
} filter_codes;

uchar paeth_algorithm(int a, int b, int c)
{
	int p = a + b - c;
	int pa = abs(p - a);
	int pb = abs(p - b);
	int pc = abs(p - c);
	if (pa <= pb && pa <= pc)
	{
		return a;
	}
	else if (pb <= pc)
	{
		return b;
	}
	else
	{
		return c;
	}
}

int int_from_bytes(int l, int r, const uchar *number)
{
	int result = 0;
	int ratio = 1;
	for (int i = r - 1; i >= l; i--)
	{
		result += (int)number[i] * ratio;
		ratio *= 256;
	}
	return result;
}

bool compare_name(const uchar *name, const uchar *ref)
{
	for (int i = 0; i < 4; i++)
	{
		if (name[i] != ref[i])
		{
			return 0;
		}
	}
	return 1;
}

int read_signature(FILE *f)
{
	uchar signatureRef[8] = { 137, 80, 78, 71, 13, 10, 26, 10 };
	uchar signature[8];
	if (fread(signature, 1, 8, f) != 8)
	{
		return -1;
	}
	for (int i = 0; i < 8; i++)
	{
		if (signature[i] != signatureRef[i])
		{
			return -1;
		}
	}
	return 0;
}

bool is_neccesary(uchar *name)
{
	if (islower(name[0]))
	{
		return 0;
	}
	return 1;
}

bool check_type(const uchar *name, const int type)
{
	if (type == IHDR)
	{
		uchar ref[4] = { 'I', 'H', 'D', 'R' };
		return compare_name(name, ref);
	}
	else if (type == PLTE)
	{
		uchar ref[4] = { 'P', 'L', 'T', 'E' };
		return compare_name(name, ref);
	}
	else if (type == IDAT)
	{
		uchar ref[4] = { 'I', 'D', 'A', 'T' };
		return compare_name(name, ref);
	}
	else if (type == IEND)
	{
		uchar ref[4] = { 'I', 'E', 'N', 'D' };
		return compare_name(name, ref);
	}
	return 0;
}

chunk *read_chunk(FILE *f, int *exitCode)
{
	uchar tempLen[4];
	if (fread(tempLen, sizeof(uchar), 4, f) != 4)
	{
		fprintf(stderr, "Error! This is not a PNG");
		*exitCode = ERROR_DATA_INVALID;
		return NULL;
	}
	int len = int_from_bytes(0, 4, tempLen);
	if (len < 0)
	{
		fprintf(stderr, "Error! This is not a PNG. Chunk length is negative");
		*exitCode = ERROR_DATA_INVALID;
		return NULL;
	}
	chunk *currentChunk = malloc(sizeof(*currentChunk) + sizeof(uchar) * len);
	if (currentChunk == NULL)
	{
		fprintf(stderr, "Error! Not enough memory to allocate data.");
		*exitCode = ERROR_OUT_OF_MEMORY;
		return NULL;
	}
	currentChunk->length = len;
	if (fread(&(currentChunk->type), sizeof(uchar), 4, f) != 4)
	{
		free(currentChunk);
		fprintf(stderr, "Error! This is not a PNG");
		*exitCode = ERROR_DATA_INVALID;
		return NULL;
	}
	if (fread(&(currentChunk->data), sizeof(uchar), currentChunk->length, f) != currentChunk->length)
	{
		free(currentChunk);
		fprintf(stderr, "Error! This is not a PNG");
		*exitCode = ERROR_DATA_INVALID;
		return NULL;
	}
	if (fread(&(currentChunk->CRC), sizeof(uint), 1, f) != 1)
	{
		free(currentChunk);
		fprintf(stderr, "Error! This is not a PNG");
		*exitCode = ERROR_DATA_INVALID;
		return NULL;
	}
	*exitCode = SUCCESS;
	return currentChunk;
}

int get_ihdr_data(chunk *ihdr, uint *width, uint *height, uchar *bitDepth, uchar *colorType)
{
	// Checking the name
	if (check_type(ihdr->type, IHDR) == 0)
	{
		fprintf(stderr, "This is not a PNG. Expected first IHDR chunk");
		return ERROR_DATA_INVALID;
	}

	if (ihdr->length != 13)
	{
		fprintf(stderr, "This is not a PNG. The HDR chunk has a length not equal to 13 bytes");
		return ERROR_DATA_INVALID;
	}
	// Checking the resolution
	*width = int_from_bytes(0, 4, ihdr->data);
	*height = int_from_bytes(4, 8, ihdr->data);
	if (*width < 1 || *height < 1)
	{
		free(ihdr);
		fprintf(stderr, "WRONG IMAGE RESOLUTION");
		return ERROR_DATA_INVALID;
	}
	// Cheking the bit depth
	*bitDepth = ihdr->data[8];
	if (*bitDepth != 8)
	{
		fprintf(stderr, "UNSUPPORTED BIT DEPTH");
		return ERROR_UNSUPPORTED;
	}
	*colorType = ihdr->data[9];
	if (!(*colorType == 0 || *colorType == 2 || *colorType == 3))
	{
		fprintf(stderr, "UNSUPPORTED COLOR TYPE");
		return ERROR_UNSUPPORTED;
	}
	return 0;
}

#if defined(ZLIB)
int uncompressData(uchar *data, size_t size, uchar *result, size_t uncompressedSize)
{
	if (uncompress(result, &uncompressedSize, data, size) != Z_OK)
	{
		return -1;
	}
	return 0;
}

#elif defined(LIBDEFLATE)
int uncompressData(uchar *data, size_t size, uchar *result, size_t uncompressedSize)
{
	struct libdeflate_decompressor *decompressor = libdeflate_alloc_decompressor();
	if (decompressor == NULL)
	{
		return -1;
	}
	if (libdeflate_zlib_decompress(decompressor, data, size, result, uncompressedSize, NULL) != LIBDEFLATE_SUCCESS)
	{
		return -1;
	}
	return 0;
}
#elif defined(ISAL)
int uncompressData(uchar *data, size_t size, uchar *result, size_t uncompressedSize)
{
	struct inflate_state state;
	isal_inflate_init(&state);
	state.avail_in = size;
	state.next_in = data;
	state.next_out = result;
	state.avail_out = uncompressedSize;
	state.crc_flag = ISAL_ZLIB;
	if (isal_inflate(&state) != ISAL_DECOMP_OK)
	{
		return -1;
	}
	return 0;
}
#else
#error "Unsupported library."
#endif

int filter(uint width, uint height, uchar colorType, uchar *data, int isPLTE)
{
	int pixLength;
	if (colorType == 0 || colorType == 2)
	{
		pixLength = colorType + 1;
	}
	else
	{
		pixLength = colorType;
	}
	uint rowBytes = width * pixLength + 1;
	if (isPLTE)
	{
		rowBytes = width + 1;
	}
	for (int i = 0; i < height; i++)
	{
		uchar filterType = data[i * rowBytes];
		if (filterType != 0 && filterType != SUB && filterType != UP && filterType != AVG && filterType != PAETH)
		{
			return -1;
		}
		for (int j = 1; j < rowBytes; j++)
		{
			if (filterType == 0)
			{
				continue;
			}
			else if (filterType == SUB && j > colorType + 1)
			{
				data[rowBytes * i + j] += data[rowBytes * i + j - (colorType + 1)];
			}
			else if (filterType == UP && i > 0)
			{
				data[rowBytes * i + j] += data[rowBytes * (i - 1) + j];
			}
			else if (filterType == AVG && i > 0)
			{
				if (j > colorType + 1)
				{
					data[rowBytes * i + j] +=
						((int)data[rowBytes * (i - 1) + j] + (int)data[rowBytes * i + j - (colorType + 1)]) / 2;
				}
				else
				{
					data[rowBytes * i + j] += ((int)data[rowBytes * (i - 1) + j]) / 2;
				}
			}
			else if (filterType == PAETH)
			{
				if (i > 0 && j > colorType + 1)
				{
					data[rowBytes * i + j] += paeth_algorithm(
						data[rowBytes * i + j - (colorType + 1)],
						data[rowBytes * (i - 1) + j],
						data[rowBytes * (i - 1) + j - (colorType + 1)]);
				}
				else if (i > 0 && j <= colorType + 1)
				{
					data[rowBytes * i + j] += data[rowBytes * (i - 1) + j];
				}
				else
				{
					data[rowBytes * i + j] += data[rowBytes * i + j - (colorType + 1)];
				}
			}
		}
	}
	return SUCCESS;
}

int get_PNM_type(uint colorType, const uchar *palette, size_t palette_sizeByte)
{
	if (colorType == 0)
	{
		return 5;
	}
	else if (colorType == 2)
	{
		return 6;
	}
	else
	{
		for (int i = 0; i < palette_sizeByte; i += 3)
		{
			if (palette[i] != palette[i + 1] || palette[i + 1] != palette[i + 2])
			{
				return 6;
			}
		}
		return 5;
	}
}

int convert_to_PNM_PLTE(FILE *out, uint width, uint height, const uchar *data, uchar *palette, size_t palette_sizeByte, size_t raw_size, int PNM_type)
{
	fprintf(out, "P%i\n%i %i\n%i\n", PNM_type, width, height, 255);
	if (PNM_type == 6)
	{
		for (int i = 0; i < raw_size; i++)
		{
			if (i % (width + 1))
			{
				if (data[i] >= palette_sizeByte / 3)
				{
					return -1;
				}
				for (int j = 0; j < 3; j++)
				{
					if (fwrite(&palette[data[i] * 3 + j], sizeof(uchar), 1, out) != 1)
					{
						return -2;
					}
				}
			}
		}
	}
	else
	{
		for (int i = 0; i < raw_size; i++)
		{
			if (i % (width + 1))
			{
				if (data[i] > palette_sizeByte / 3)
				{
					return -1;
				}
				if (fwrite(&palette[data[i] * 3], sizeof(uchar), 1, out) != 1)
				{
					return -2;
				}
			}
		}
	}
	return 0;
}

int convert_to_PNM(FILE *out, uint colorType, uint width, uint height, const uchar *data, uchar *palette, size_t palette_sizeByte, size_t output_size)
{
	int PNM_type = get_PNM_type(colorType, palette, palette_sizeByte);
	if (colorType == 3)
	{
		if (convert_to_PNM_PLTE(out, width, height, data, palette, palette_sizeByte, output_size, PNM_type) != 0)
		{
			return -1;
		}
	}
	else
	{
		uint rowBytes = width * (colorType + 1) + 1;
		fprintf(out, "P%i\n%i %i\n%i\n", PNM_type, width, height, 255);
		for (int i = 0; i < height; i++)
		{
			if (fwrite(&data[(i * rowBytes) + 1], sizeof(uchar), rowBytes - 1, out) != rowBytes - 1)
			{
				return -2;
			}
		}
	}
	return 0;
}

int main(int argc, char *argv[])
{
	int exitCode = SUCCESS;
	FILE *input = NULL;
	FILE *out = NULL;

	if (argc != 3)
	{
		fprintf(stderr, "Error! Expected 2 arguments: input file and output file.");
		exitCode = ERROR_PARAMETER_INVALID;
		goto Free;
	}

	input = fopen(argv[1], "rb");
	if (input == NULL)
	{
		fprintf(stderr, "FILE NOT FOUND");
		exitCode = ERROR_CANNOT_OPEN_FILE;
		goto Free;
	}

	if (read_signature(input) != 0)
	{
		fprintf(stderr, "This is not a PNG");
		exitCode = ERROR_DATA_INVALID;
		goto Free;
	}

	chunk *ihdr = read_chunk(input, &exitCode);

	if (ihdr == NULL)
	{
		// NOTE: The error message is output by the function read_chunk. The return code is changed by the function
		// itself.
		goto Free;
	}
	uint width;
	uint height;
	uchar depth;
	uchar colorType;

	exitCode = get_ihdr_data(ihdr, &width, &height, &depth, &colorType);
	if (exitCode != 0)
	{
		// NOTE: the error message is output by the function
		free(ihdr);
		goto Free;
	}
	free(ihdr);

	uchar *palette = NULL;
	size_t palette_sizeByte = 0;

	int plteExpected = UNEXPECTED_PLTE;
	if (colorType == 2)
	{
		plteExpected = UNNECESSARY_PLTE;
	}
	else if (colorType == 3)
	{
		plteExpected = NECESSARY_PLTE;
	}

	size_t dataSize = 8192;
	size_t takenBytes = 0;
	uchar *imageData = malloc(dataSize);
	if (imageData == NULL)
	{
		fprintf(stderr, "Error! Not enough memory to allocate data.");
		exitCode = ERROR_OUT_OF_MEMORY;
		goto Free;
	}

	for (;;)
	{
		if (feof(input))
		{
			free(imageData);
			if (palette)
			{
				free(palette);
			}
			fprintf(stderr, "Error! Missing IEND chunk after IDAT.");
			exitCode = ERROR_DATA_INVALID;
			goto Free;
		}

		chunk *currCh = read_chunk(input, &exitCode);

		if (currCh == NULL)
		{
			// NOTE: The error message is output by the function read_chunk. The return code is changed by the function
			// itself.
			if (palette)
			{
				free(palette);
			}
			free(imageData);
			goto Free;
		}
		if (check_type(currCh->type, PLTE))
		{
			if (plteExpected == UNEXPECTED_PLTE)
			{
				free(currCh);
				free(imageData);
				if (palette)
				{
					free(palette);
				}
				fprintf(stderr, "Error! Unexpected(extra) PLTE chunk in PNG");
				exitCode = ERROR_DATA_INVALID;
				goto Free;
			}
			else if (plteExpected == NECESSARY_PLTE)
			{
				plteExpected = UNEXPECTED_PLTE;
				if (currCh->length % 3 != 0 || currCh->length > 768)
				{
					free(imageData);
					free(currCh);
					fprintf(stderr, "Error! PLTE chunk incorrect");
					exitCode = ERROR_DATA_INVALID;
					goto Free;
				}
				palette_sizeByte = currCh->length;
				palette = malloc(currCh->length);
				memcpy(palette, currCh->data, currCh->length);
			}
			else
			{
				plteExpected = UNEXPECTED_PLTE;
			}
		}
		else if (check_type(currCh->type, IDAT))
		{
			if (plteExpected == NECESSARY_PLTE)
			{
				free(currCh);
				free(imageData);
				fprintf(stderr, "Error! This is not a PNG. Expected PLTE chunk before IDAT.");
				exitCode = ERROR_DATA_INVALID;
				goto Free;
			}
			else if (plteExpected == UNNECESSARY_PLTE)
			{
				plteExpected = UNEXPECTED_PLTE;
			}
			if (currCh->length > 0)
			{
				size_t temp = dataSize;
				while (currCh->length + takenBytes >= dataSize)
				{
					dataSize *= 2;
				}
				if (dataSize != temp)
				{
					uchar *tempData = realloc(imageData, sizeof(uchar) * dataSize);
					if (tempData == NULL)
					{
						free(currCh);
						free(imageData);
						if (palette)
						{
							free(palette);
						}
						fprintf(stderr, "Error! Not enough memory to allocate data.");
						exitCode = ERROR_OUT_OF_MEMORY;
						goto Free;
					}
					else
					{
						imageData = tempData;
					}
				}
				memcpy(imageData + takenBytes, currCh->data, currCh->length * sizeof(uchar));
				takenBytes += currCh->length;
			}
		}
		else if (check_type(currCh->type, IEND))
		{
			if (currCh->length != 0)
			{
				free(imageData);
				free(currCh);
				if (palette)
				{
					free(palette);
				}
				goto Free;
			}
			uint rawSize = height * width * (colorType + 1) + height;
			if (colorType == 3)
			{
				rawSize = height * (width + 1);
			}
			uchar *decompressedData = malloc(rawSize);
			if (decompressedData == NULL)
			{
				free(imageData);
				free(currCh);
				if (palette)
				{
					free(palette);
				}
				fprintf(stderr, "Error! Not enough memory to allocate data.");
				exitCode = ERROR_OUT_OF_MEMORY;
				goto Free;
			}
			if (uncompressData(imageData, takenBytes, decompressedData, rawSize) == -1)
			{
				fprintf(stderr, "Error! Unable to decompress data.");
				free(imageData);
				free(currCh);
				free(decompressedData);
				if (palette)
				{
					free(palette);
				}
				exitCode = ERROR_DATA_INVALID;
				goto Free;
			}
			out = fopen(argv[2], "wb");
			if (out == NULL)
			{
				fprintf(stderr, "OUTPUT FILE NOT FOUND");
				free(imageData);
				free(currCh);
				free(decompressedData);
				if (palette)
				{
					free(palette);
				}
				exitCode = ERROR_CANNOT_OPEN_FILE;
				goto Free;
			}
			int isPlte = colorType == 3;
			if (filter(width, height, colorType, decompressedData, isPlte) == -1)
			{
				fprintf(stderr, "Error! Unknown filter type found(Check image data)");
				free(imageData);
				free(currCh);
				free(decompressedData);
				if (palette)
				{
					free(palette);
				}
				exitCode = ERROR_DATA_INVALID;
				goto Free;
			}
			int error = convert_to_PNM(out, colorType, width, height, decompressedData, palette, palette_sizeByte, rawSize);
			if (error != 0)
			{
				if (error == -1)
				{
					fprintf(stderr, "Error! Byte value is greater than PLTE color number");
				}
				else
				{
					fprintf(stderr, "Error! An error occurred while writing to the output file");
				}
				free(imageData);
				free(currCh);
				free(decompressedData);
				if (palette)
				{
					free(palette);
				}
				exitCode = ERROR_DATA_INVALID;
				goto Free;
			}
			free(imageData);
			free(currCh);
			free(decompressedData);
			if (palette)
			{
				free(palette);
			}
			break;
		}
		else if (is_neccesary(currCh->type))
		{
			fprintf(stderr, "UNEXPECTED CRITICAL CHUNK");
			free(imageData);
			free(currCh);
			if (palette)
			{
				free(palette);
			}
			exitCode = ERROR_DATA_INVALID;
			goto Free;
		}
		free(currCh);
	}

	fclose(input);
	fclose(out);
	return exitCode;

Free:
	if (input)
	{
		fclose(input);
	}
	if (out)
	{
		fclose(out);
	}
	return exitCode;
}
