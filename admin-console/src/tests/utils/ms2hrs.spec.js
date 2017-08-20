import ms2hrs from '../../utils/ms2hrs'

describe('ms to hrs', () => {
  test('should handle empty', () => {
    expect(ms2hrs()).toBe('')
  })

  test('should return 1m', () => {
    expect(ms2hrs(60000)).toBe('0d 0h 1m ')
  })

  test('should return 1h', () => {
    expect(ms2hrs(60000 * 60)).toBe('0d 1h 0m ')
  })

  test('should return 1d', () => {
    expect(ms2hrs(60000 * 60 * 24)).toBe('1d 0h 0m ')
  })
})
