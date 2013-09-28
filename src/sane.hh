#ifndef _SANE_HH
#define _SANE_HH

#include <cstdio>
#include <cstdlib>
#include <cstdint>
#include <string>
#include <iostream>
#include <vector>
#include <fstream>
using namespace std;

#define show(x) \
  cout << __FILE__ ":" << __LINE__ << ": " << #x << " = " << x << "\n"

typedef int8_t i8;
typedef int16_t i16;
typedef int32_t i32;
typedef int64_t i64;
typedef uint8_t u8;
typedef uint16_t u16;
typedef uint32_t u32;
typedef uint64_t u64;

#endif

