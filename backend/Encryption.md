### AES (Advanced Encryption Standard)

- AES is a symmetric encryption algorithm, meaning the same key is used for both encryption and decryption.
- It operates on blocks of data (128-bit blocks) and is highly secure and efficient.
- AES is commonly used in many secure applications, including file encryption.

#### Key Features of AES:

- Key Size: AES can use 128, 192, or 256-bit keys. In this example, we're using AES-128, which means the key is 128 bits
  long.
- Block Size: AES operates on 128-bit blocks of data. Even though the data may not fit perfectly into 128-bit chunks,
  it’s padded to make it fit.

### CBC (Cipher Block Chaining) Mode

AES can be used in different modes, and CBC is one of the most commonly used modes for file encryption.
CBC mode ensures that each block of plaintext is combined with the previous block of ciphertext before encryption,
making the encryption more secure.

#### How CBC Works:

    The plaintext is split into fixed-size blocks (128 bits in AES).
    The first block is XORed (combined) with a random Initialization Vector (IV).
    This combined data is encrypted using AES, producing the first block of ciphertext.
    Each subsequent plaintext block is XORed with the previous ciphertext block before encryption.
    The process continues for all blocks, making the encryption stronger because each block depends on the previous one.

#### Why CBC is Secure:

- The use of the Initialization Vector (IV) ensures that even if two identical messages are encrypted, their ciphertexts
  will be different.
- CBC avoids patterns in the ciphertext, making it resistant to many attacks.

### PKCS5 Padding

- PKCS5 padding is used to ensure that the data being encrypted fits into 128-bit blocks.
- If the last block of plaintext is shorter than 128 bits, extra bytes (padding) are added to make it fit.
- During decryption, the padding is removed to recover the original plaintext.

### Initialization Vector (IV)

- An IV is a random block of data that is used to initialize the encryption process in CBC mode.
- The IV ensures that the first block of plaintext is randomized, even if the same plaintext is encrypted multiple times
  with the same key.
- Without the IV, two identical messages would result in identical ciphertexts, which could reveal patterns.

#### Key Points About IV:

- It must be random and unique for each encryption operation.
- It is usually the same size as the block size (16 bytes for AES-128).
- The IV is not secret but needs to be stored or transmitted along with the ciphertext for proper decryption.

### How Encryption Works in Our Code

#### Encryption Process:

    We use a 128-bit AES key, generated from the unique code, to encrypt each file.
    A random IV is generated and used to ensure each file’s encryption is unique.
    CBC mode ensures that each block of data is encrypted based on the previous block, adding more security.
    PKCS5 padding ensures that the file size is a multiple of the block size.
    The encrypted file is stored with the IV prepended to it. This way, we can retrieve it later for decryption.

#### Decryption Process:

    When decrypting, we read the IV from the beginning of the encrypted file.
    The same AES key (generated from the unique code) is used.
    The file is decrypted block by block using CBC mode.
    The padding is removed to get the original file back.

### Why AES with CBC is Secure

#### Symmetric Encryption:

    Only those who know the key (in this case, the unique code) can decrypt the file.

#### Random IV:

    The initialization vector ensures that even if the same file is encrypted multiple times, the ciphertext will be different each time.

#### CBC Mode:

    Makes it resistant to block replay attacks, where an attacker tries to reuse previously encrypted blocks.

#### Padding:

    Ensures the file is always a multiple of the block size, regardless of its original size.