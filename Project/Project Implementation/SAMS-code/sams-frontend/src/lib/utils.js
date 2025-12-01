/*
 * utils.js - Utility Functions
 *
 * helper functions used throughout teh app
 * cn function merges tailwind classes properly - handles conflicts
 * works for now - pretty simple but useful
 */

import { clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

// cn - class name utility
// merges tailwind classes and resolves conflicts
// example: cn('p-4', 'p-2') => 'p-2' (later value wins)
export function cn(...inputs) {
  return twMerge(clsx(inputs))
}
